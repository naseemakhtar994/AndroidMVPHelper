/*
 * Copyright 2016 Ufkoku (https://github.com/Ufkoku/AndroidMVPHelper)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ufkoku.mvp.viewstate.autosavable;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.element.Modifier;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.JavaFileObject;

import java.io.BufferedWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SupportedAnnotationTypes({"*"})
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class AutosavableProcessor extends AbstractProcessor {

    private static final Pattern GENERIC_TYPE_EXTRACTOR = Pattern.compile("<(.*)>");

    private static final String PACKAGE = AutosavableProcessor.class.getPackage().getName();

    private static final String SAVER_SUFFIX = "Saver";
    private static final String SAVE_HANDLER_NAME = "SaveHandler";
    private static final String AUTO_SAVABLE_NAME = "AutoSavableViewState";

    private static final String I_MVP_VIEW_FULL_NAME = "com.ufkoku.mvp_base.view.IMvpView";
    private static final String SAVABLE_INTERFACE_FULL_NAME = "com.ufkoku.mvp_base.viewstate.ISavableViewState";

    private static final String BUNDLE_FULL_NAME = "android.os.Bundle";
    private static final String PARSABLE_FULL_NAME = "android.os.Parcelable";

    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        try {
            List<SaverPair> saverNames = new ArrayList<>();

            Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(AutoSavable.class);
            if (elements.size() > 0) {
                for (Element element : elements) {
                    if (element.getKind() == ElementKind.CLASS) {
                        if (!element.getModifiers().contains(Modifier.ABSTRACT)) {
                            TypeElement typeElement = (TypeElement) element;
                            saverNames.add(createSaver(typeElement));
                        }
                    }
                }
            }

            createSaveHandler(saverNames);

            createAutoSavableViewState();

            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private SaverPair createSaver(TypeElement element) throws IOException {
        String name = element.getSimpleName() + SAVER_SUFFIX;

        final String OUT_STATE = "outState";
        final String IN_STATE = "inState";
        final String STATE = "state";

        MethodSpec.Builder saveSpecBuilder = MethodSpec.methodBuilder("save")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addParameter(ClassName.bestGuess(element.getQualifiedName().toString()), STATE)
                .addParameter(ClassName.bestGuess(BUNDLE_FULL_NAME), IN_STATE)
                .returns(void.class);

        MethodSpec.Builder restoreSpecBuilder = MethodSpec.methodBuilder("restore")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addParameter(ClassName.bestGuess(element.getQualifiedName().toString()), STATE)
                .addParameter(ClassName.bestGuess(BUNDLE_FULL_NAME), OUT_STATE)
                .returns(void.class);

        List<? extends Element> elements = element.getEnclosedElements();
        if (elements.size() > 0) {

            boolean allPublic = true;
            int gettersCount = 0;
            int settersCount = 0;

            Map<String, VariableData> variableDataMap = new HashMap<>();
            for (Element field : elements) {
                if (field instanceof VariableElement && field.getKind() == ElementKind.FIELD) {
                    if (!field.getModifiers().contains(Modifier.STATIC)
                            && !field.getModifiers().contains(Modifier.FINAL)
                            && !field.getModifiers().contains(Modifier.TRANSIENT)) {

                        final String fieldName = field.getSimpleName().toString();
                        variableDataMap.put(fieldName, new VariableData((VariableElement) field));

                        allPublic = allPublic && field.getModifiers().contains(Modifier.PUBLIC);
                    }
                }
            }

            if (!allPublic) {
                for (Element method : elements) {
                    if (method instanceof ExecutableElement && method.getKind() == ElementKind.METHOD) {
                        final String methodName = method.getSimpleName().toString();
                        if (methodName.length() > 3) {
                            if (methodName.startsWith("get")) {
                                String fieldName = methodName.substring(3, 4).toLowerCase() + methodName.substring(4);
                                VariableData group = variableDataMap.get(fieldName);
                                if (group != null) {
                                    group.setGetter((ExecutableElement) method);
                                    gettersCount++;
                                }
                            } else if (methodName.startsWith("set")) {
                                String fieldName = methodName.substring(3, 4).toLowerCase() + methodName.substring(4);
                                VariableData group = variableDataMap.get(fieldName);
                                if (group != null) {
                                    group.setSetter((ExecutableElement) method);
                                    settersCount++;
                                }
                            }
                        }
                    }
                }

                if (gettersCount != variableDataMap.size()) {
                    saveSpecBuilder.addCode("try {\n");
                }

                if (settersCount != variableDataMap.size()) {
                    restoreSpecBuilder.addCode("try {\n");
                }
            }

            for (VariableData variableData : variableDataMap.values()) {

                VariableElement field = variableData.getElement();
                TypeMirror fieldType = field.asType();
                final String strFieldType = fieldType.toString();

                if (strFieldType.equals(BUNDLE_FULL_NAME)) {
                    writeCode(saveSpecBuilder, restoreSpecBuilder, variableData, STATE, IN_STATE, OUT_STATE, "putBundle", "getBundle");
                } else if (strFieldType.equals(byte.class.getCanonicalName())) {
                    writeCode(saveSpecBuilder, restoreSpecBuilder, variableData, STATE, IN_STATE, OUT_STATE, "putByte", "getByte", "setByte", "getByte");
                } else if (strFieldType.equals(byte[].class.getCanonicalName())) {
                    writeCode(saveSpecBuilder, restoreSpecBuilder, variableData, STATE, IN_STATE, OUT_STATE, "putByteArray", "getByteArray");
                } else if (strFieldType.equals(char.class.getCanonicalName())) {
                    writeCode(saveSpecBuilder, restoreSpecBuilder, variableData, STATE, IN_STATE, OUT_STATE, "putChar", "getChar", "setChar", "getChar");
                } else if (strFieldType.equals(char[].class.getCanonicalName())) {
                    writeCode(saveSpecBuilder, restoreSpecBuilder, variableData, STATE, IN_STATE, OUT_STATE, "putCharArray", "getCharArray");
                } else if (strFieldType.equals(float.class.getCanonicalName())) {
                    writeCode(saveSpecBuilder, restoreSpecBuilder, variableData, STATE, IN_STATE, OUT_STATE, "putFloat", "getFloat", "setFloat", "getFloat");
                } else if (strFieldType.equals(float[].class.getCanonicalName())) {
                    writeCode(saveSpecBuilder, restoreSpecBuilder, variableData, STATE, IN_STATE, OUT_STATE, "putFloatArray", "getFloatArray");
                } else if (strFieldType.equals(int.class.getCanonicalName())) {
                    writeCode(saveSpecBuilder, restoreSpecBuilder, variableData, STATE, IN_STATE, OUT_STATE, "putInt", "getInt", "setInt", "getInt");
                } else if (strFieldType.equals(int[].class.getCanonicalName())) {
                    writeCode(saveSpecBuilder, restoreSpecBuilder, variableData, STATE, IN_STATE, OUT_STATE, "putIntArray", "getIntArray");
                } else if (strFieldType.equals(short.class.getCanonicalName())) {
                    writeCode(saveSpecBuilder, restoreSpecBuilder, variableData, STATE, IN_STATE, OUT_STATE, "putShort", "getShort", "setShort", "getShort");
                } else if (strFieldType.equals(short[].class.getCanonicalName())) {
                    writeCode(saveSpecBuilder, restoreSpecBuilder, variableData, STATE, IN_STATE, OUT_STATE, "putShortArray", "getShortArray");
                } else if (strFieldType.equals(long.class.getCanonicalName())) {
                    writeCode(saveSpecBuilder, restoreSpecBuilder, variableData, STATE, IN_STATE, OUT_STATE, "putLong", "getLong", "setLong", "getLong");
                } else if (strFieldType.equals(long[].class.getCanonicalName())) {
                    writeCode(saveSpecBuilder, restoreSpecBuilder, variableData, STATE, IN_STATE, OUT_STATE, "putLongArray", "getLongArray");
                } else if (strFieldType.equals(double.class.getCanonicalName())) {
                    writeCode(saveSpecBuilder, restoreSpecBuilder, variableData, STATE, IN_STATE, OUT_STATE, "putDouble", "getDouble", "setDouble", "getDouble");
                } else if (strFieldType.equals(double[].class.getCanonicalName())) {
                    writeCode(saveSpecBuilder, restoreSpecBuilder, variableData, STATE, IN_STATE, OUT_STATE, "putDoubleArray", "getDoubleArray");
                } else {
                    boolean saved = false;

                    Types typeUtils = processingEnv.getTypeUtils();
                    Elements elementUtils = processingEnv.getElementUtils();

                    if (!saved) {
                        TypeElement charSequenceElement = elementUtils.getTypeElement("java.lang.CharSequence");
                        if (typeUtils.isAssignable(fieldType, charSequenceElement.asType())) {
                            writeCode(saveSpecBuilder, restoreSpecBuilder, variableData, STATE, IN_STATE, OUT_STATE, "putCharSequence", "getCharSequence");
                            saved = true;
                        }
                    }

                    TypeElement parcelableElement = elementUtils.getTypeElement(PARSABLE_FULL_NAME);
                    TypeMirror parcelableType = parcelableElement.asType();

                    if (!saved) {
                        if (typeUtils.isAssignable(fieldType, parcelableType)) {
                            writeCode(saveSpecBuilder, restoreSpecBuilder, variableData, STATE, IN_STATE, OUT_STATE, "putParcelable", "getParcelable");
                            saved = true;
                        }
                    }

                    if (!saved) {
                        DeclaredType arrayListType = typeUtils.getDeclaredType(
                                elementUtils.getTypeElement("java.util.ArrayList"),
                                typeUtils.getWildcardType(parcelableType, null));

                        if (typeUtils.isAssignable(fieldType, arrayListType)) {
                            TypeElement genericTypeElement = getGenericTypeOfTargetSuperClass(elementUtils, fieldType, arrayListType);
                            if (genericTypeElement != null) {
                                writeCode(saveSpecBuilder, restoreSpecBuilder, variableData, STATE, IN_STATE, OUT_STATE,
                                        "putParcelableArrayList", String.format("<%s>getParcelableArrayList", genericTypeElement.getQualifiedName().toString()));
                                saved = true;
                            }
                        }

                    }

                    if (!saved) {
                        DeclaredType sparseArrayType = typeUtils.getDeclaredType(
                                elementUtils.getTypeElement("android.util.SparseArray"),
                                typeUtils.getWildcardType(parcelableType, null));

                        if (typeUtils.isAssignable(fieldType, sparseArrayType)) {
                            TypeElement genericTypeElement = getGenericTypeOfTargetSuperClass(elementUtils, fieldType, sparseArrayType);
                            if (genericTypeElement != null) {
                                writeCode(saveSpecBuilder, restoreSpecBuilder, variableData, STATE, IN_STATE, OUT_STATE,
                                        "putSparseParcelableArray", String.format("<%s>getSparseParcelableArray", genericTypeElement.getQualifiedName().toString()));
                                saved = true;
                            }
                        }
                    }

                    if (!saved) {
                        if (strFieldType.endsWith("[]")) {
                            TypeElement arrayTypeElement = elementUtils.getTypeElement(strFieldType.substring(0, strFieldType.length() - 2));
                            if (typeUtils.isAssignable(arrayTypeElement.asType(), parcelableType)) {
                                writeCode(saveSpecBuilder, restoreSpecBuilder, variableData, STATE, IN_STATE, OUT_STATE, "putParcelableArray", "getParcelableArray");
                                saved = true;
                            }
                        }
                    }

                    if (!saved) {
                        writeCode(saveSpecBuilder, restoreSpecBuilder, variableData, STATE, IN_STATE, OUT_STATE, "putSerializable", "getSerializable");
                        saved = true;
                    }

                }

                saveSpecBuilder.addCode("\n");
                restoreSpecBuilder.addCode("\n");
            }

            if (!allPublic) {
                if (gettersCount != variableDataMap.size()) {
                    saveSpecBuilder.addCode("} " + createCatch(NoSuchFieldException.class));
                    saveSpecBuilder.addCode(" " + createCatch(IllegalAccessException.class));
                    saveSpecBuilder.addCode("\n");
                }

                if (settersCount != variableDataMap.size()) {
                    restoreSpecBuilder.addCode("} " + createCatch(NoSuchFieldException.class));
                    restoreSpecBuilder.addCode(" " + createCatch(IllegalAccessException.class));
                    restoreSpecBuilder.addCode("\n");
                }
            }
        }

        MethodSpec saveSpec = saveSpecBuilder.build();
        MethodSpec restoreSpec = restoreSpecBuilder.build();

        TypeSpec typeSpec = TypeSpec.classBuilder(name)
                .addMethod(saveSpec)
                .addMethod(restoreSpec)
                .build();

        JavaFile javaFile = JavaFile.builder(PACKAGE, typeSpec).build();
        javaFile.writeTo(processingEnv.getFiler());

        return new SaverPair(
                ((PackageElement) element.getEnclosingElement()).getQualifiedName().toString(),
                element.getSimpleName().toString(),
                name);
    }

    private void createSaveHandler(List<SaverPair> savers) throws IOException {
        MethodSpec.Builder saveSpecBuilder = MethodSpec.methodBuilder("save")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addParameter(Object.class, "state")
                .addParameter(ClassName.bestGuess(BUNDLE_FULL_NAME), "outState")
                .returns(void.class);

        for (int i = 0; i < savers.size(); i++) {
            SaverPair saver = savers.get(i);
            System.out.println("Processing saver: " + saver.getOriginalPackage() + "." + saver.getOriginalName() + "/" + saver.getSaverName());
            saveSpecBuilder.beginControlFlow("if (state.getClass().getSimpleName().equals($S))", saver.getOriginalName());
            saveSpecBuilder.addStatement("$L.save(($T) state, outState)",
                    saver.getSaverName(),
                    ClassName.bestGuess(
                            createFullName(saver.getOriginalPackage(), saver.getOriginalName())));
            if (i != savers.size() - 1) {
                saveSpecBuilder.addCode("} else ");
            } else {
                saveSpecBuilder.endControlFlow();
            }
        }

        MethodSpec saveSpec = saveSpecBuilder.build();

        MethodSpec.Builder restoreSpecBuilder = MethodSpec.methodBuilder("restore")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addParameter(Object.class, "state")
                .addParameter(ClassName.bestGuess(BUNDLE_FULL_NAME), "inState")
                .returns(void.class);

        for (int i = 0; i < savers.size(); i++) {
            SaverPair saver = savers.get(i);
            System.out.println("Processing saver: " + saver.getOriginalPackage() + "." + saver.getOriginalName() + "/" + saver.getSaverName());
            restoreSpecBuilder.beginControlFlow("if (state.getClass().getSimpleName().equals($S))", saver.getOriginalName());
            restoreSpecBuilder.addStatement("$L.restore(($T) state, inState)",
                    saver.getSaverName(),
                    ClassName.bestGuess(
                            createFullName(saver.getOriginalPackage(), saver.getOriginalName())));
            if (i != savers.size() - 1) {
                restoreSpecBuilder.addCode("} else ");
            } else {
                restoreSpecBuilder.endControlFlow();
            }
        }

        MethodSpec restoreSpec = restoreSpecBuilder.build();

        TypeSpec.Builder typeSpecBuilder = TypeSpec.classBuilder(SAVE_HANDLER_NAME)
                .addModifiers(Modifier.PUBLIC)
                .addMethod(saveSpec)
                .addMethod(restoreSpec);

        TypeSpec typeSpec = typeSpecBuilder.build();

        JavaFile javaFile = JavaFile.builder(PACKAGE, typeSpec).build();
        javaFile.writeTo(System.out);
        javaFile.writeTo(processingEnv.getFiler());
    }

    private void createAutoSavableViewState() throws IOException {
        JavaFileObject jfo = processingEnv.getFiler().createSourceFile(createFullName(PACKAGE, AUTO_SAVABLE_NAME));
        BufferedWriter writer = new BufferedWriter(jfo.openWriter());

        writer.append("package " + PACKAGE + ";");
        writer.newLine();
        writer.newLine();

        writer.append(createImport(BUNDLE_FULL_NAME));
        writer.newLine();
        writer.newLine();

        writer.append(createImport(I_MVP_VIEW_FULL_NAME));
        writer.newLine();
        writer.append(createImport(SAVABLE_INTERFACE_FULL_NAME));
        writer.newLine();
        writer.newLine();

        writer.append(createImport("java.lang.Override"));
        writer.newLine();
        writer.newLine();

        writer.append("public abstract class AutoSavableViewState<T extends IMvpView> implements ISavableViewState<T> {");
        writer.newLine();

        writer.append("     @Override");
        writer.newLine();
        writer.append("     public void save(Bundle out){");
        writer.newLine();
        writer.append("         SaveHandler.save(this, out);");
        writer.newLine();
        writer.append("     }");
        writer.newLine();
        writer.newLine();

        writer.append("     @Override");
        writer.newLine();
        writer.append("     public void restore(Bundle inState) {");
        writer.newLine();
        writer.append("         SaveHandler.restore(this, inState);");
        writer.newLine();
        writer.append("     }");
        writer.newLine();

        writer.append("}");

        writer.flush();
        writer.close();
    }

    private String createFullName(String packageName, String name) {
        return packageName + "." + name;
    }

    private String createImport(String path) {
        return "import " + path + ";";
    }

    private String createCatch(Class<? extends Throwable> ex) {
        return "catch (" + ex.getCanonicalName() + " ex) {" +
                "\n     ex.printStackTrace();" +
                "\n}";
    }

    private void writeCode(
            MethodSpec.Builder saveSpecBuilder,
            MethodSpec.Builder restoreSpecBuilder,
            VariableData group,
            String stateVariableName,
            String saveStateName,
            String restoreStateName,
            String bundlePutMethod,
            String bundleGetMethod) {

        writeCode(
                saveSpecBuilder,
                restoreSpecBuilder,
                group,
                stateVariableName,
                saveStateName,
                restoreStateName,
                bundlePutMethod,
                bundleGetMethod,
                "set",
                "get");
    }

    private void writeCode(
            MethodSpec.Builder saveSpecBuilder,
            MethodSpec.Builder restoreSpecBuilder,
            VariableData group,
            String stateVariableName,
            String saveStateName,
            String restoreStateName,
            String bundlePutMethod,
            String bundleGetMethod,
            String reflectionSetMethod,
            String reflectionGetMethod) {

        VariableElement field = group.getElement();
        boolean isPublic = field.getModifiers().contains(Modifier.PUBLIC);

        TypeMirror fieldType = field.asType();

        final String fieldName = field.getSimpleName().toString();
        final String fFieldName = "f" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);

        if (group.getGetter() == null && !isPublic) {
            saveSpecBuilder.addStatement("$T $L = state.getClass().getDeclaredField($S)", Field.class, fFieldName, fieldName);
            saveSpecBuilder.addStatement("$L.setAccessible(true)", fFieldName);
        }

        if (group.getSetter() == null && !isPublic) {
            restoreSpecBuilder.addStatement("$T $L = state.getClass().getDeclaredField($S)", Field.class, fFieldName, fieldName);
            restoreSpecBuilder.addStatement("$L.setAccessible(true)", fFieldName);
        }

        if (isPublic) {
            saveSpecBuilder.addStatement("$L.$L($S, $L.$L)", saveStateName, bundlePutMethod, fieldName, stateVariableName, fieldName);
        } else if (group.getGetter() != null) {
            saveSpecBuilder.addStatement("$L.$L($S, $L.$L())", saveStateName, bundlePutMethod, fieldName, stateVariableName, group.getGetter().getSimpleName());
        } else {
            saveSpecBuilder.addStatement("$L.$L($S, ($L) $L.$L($L))", saveStateName, bundlePutMethod, fieldName, fieldType, fFieldName, reflectionGetMethod, stateVariableName);
        }

        if (isPublic) {
            restoreSpecBuilder.addStatement("$L.$L = ($T) $L.$L($S)", stateVariableName, fieldName, fieldType, restoreStateName, bundleGetMethod, fieldName);
        } else if (group.getSetter() != null) {
            restoreSpecBuilder.addStatement("$L.$L(($T) $L.$L($S))", stateVariableName, group.getSetter().getSimpleName(), fieldType, restoreStateName, bundleGetMethod, fieldName);
        } else {
            restoreSpecBuilder.addStatement("$L.$L($L, $L.$L($S))", fFieldName, reflectionSetMethod, stateVariableName, restoreStateName, bundleGetMethod, fieldName);
        }
    }

    private TypeElement getGenericTypeOfTargetSuperClass(Elements elementUtils, TypeMirror fieldType, TypeMirror target) {
        if (fieldType.toString().replaceAll("<.*>", "").equals(target.toString().replaceAll("<.*>", ""))) {
            Matcher matcher = GENERIC_TYPE_EXTRACTOR.matcher(fieldType.toString());
            if (matcher.find()) {
                String genericTypeName = matcher.group(1);
                return elementUtils.getTypeElement(genericTypeName);
            }
        }

        TypeElement mirrorElement = elementUtils.getTypeElement(fieldType.toString());
        if (mirrorElement != null && mirrorElement.getSuperclass() != null) {
            return getGenericTypeOfTargetSuperClass(elementUtils, mirrorElement.getSuperclass(), target);
        } else {
            return null;
        }
    }

    private static class VariableData {

        private final VariableElement element;

        private ExecutableElement getter;

        private ExecutableElement setter;

        public VariableData(VariableElement element) {
            this.element = element;
        }

        public VariableElement getElement() {
            return element;
        }

        public ExecutableElement getGetter() {
            return getter;
        }

        public void setGetter(ExecutableElement getter) {
            this.getter = getter;
        }

        public ExecutableElement getSetter() {
            return setter;
        }

        public void setSetter(ExecutableElement setter) {
            this.setter = setter;
        }
    }

    private static class SaverPair {

        private String originalPackage;
        private String originalName;

        private String saverName;

        public SaverPair(String originalPackage, String originalName, String saverName) {
            this.originalPackage = originalPackage;
            this.originalName = originalName;
            this.saverName = saverName;
        }

        public String getOriginalName() {
            return originalName;
        }

        public String getSaverName() {
            return saverName;
        }

        public String getOriginalPackage() {
            return originalPackage;
        }
    }

}
