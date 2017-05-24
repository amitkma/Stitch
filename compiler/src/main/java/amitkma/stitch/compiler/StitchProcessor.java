package amitkma.stitch.compiler;

import static javax.lang.model.element.ElementKind.METHOD;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeName;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;
import javax.tools.Diagnostic.Kind;

import amitkma.stitch.annotations.CallOnAnyThread;
import amitkma.stitch.annotations.CallOnNewThread;
import amitkma.stitch.annotations.CallOnUiThread;
import amitkma.stitch.compiler.StitchGenerator.Builder;
import amitkma.stitch.compiler.utils.Constants;

/**
 * Create by Amit Kumar on 16/5/17
 * Email : mr.doc10jl96@gmail.com
 * Company : Dot Wave Ltd.
 */

/**
 * Class which extends {@link AbstractProcessor} to process annotations. This implementation is
 * similar to <a href="https://github.com/JakeWharton/butterknife">Butterknife</a>.
 */
@AutoService(Processor.class)
public class StitchProcessor extends AbstractProcessor {

    private Messager mMessager;

    @Override
    public synchronized void init(ProcessingEnvironment env) {
        super.init(env);
        mMessager = env.getMessager();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new LinkedHashSet<>();
        types.add(CallOnAnyThread.class.getCanonicalName());
        types.add(CallOnNewThread.class.getCanonicalName());
        types.add(CallOnUiThread.class.getCanonicalName());
        return types;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment env) {
        Map<TypeElement, StitchGenerator> stitchGeneratorMap = processAnnotations(env);
        for (Entry<TypeElement, StitchGenerator> typeElementCodeGeneratorEntry : stitchGeneratorMap
                .entrySet()) {
            TypeElement typeElement = typeElementCodeGeneratorEntry.getKey();
            StitchGenerator stitchGenerator = typeElementCodeGeneratorEntry.getValue();
            JavaFile javaFile = stitchGenerator.makeFile();
            try {
                javaFile.writeTo(processingEnv.getFiler());
            } catch (IOException e) {
                mMessager.printMessage(Kind.ERROR, String
                        .format("Unable to write binding for type %s: %s",
                                typeElement.getSimpleName(),
                                e.getMessage()));
            }
        }
        return false;
    }

    private Map<TypeElement, StitchGenerator> processAnnotations(RoundEnvironment env) {
        Map<TypeElement, StitchGenerator.Builder> builderMap = new LinkedHashMap<>();

        for (Element element : env.getElementsAnnotatedWith(CallOnAnyThread.class)) {
            parseAnyThreadAnnotation(element, builderMap);
        }

        for (Element element : env.getElementsAnnotatedWith(CallOnUiThread.class)) {
            parseUiThreadAnnotation(element, builderMap);
        }

        for (Element element : env.getElementsAnnotatedWith(CallOnNewThread.class)) {
            parseNewThreadAnnotation(element, builderMap);
        }

        Deque<Entry<TypeElement, StitchGenerator.Builder>> entries = new ArrayDeque<>(
                builderMap.entrySet());
        Map<TypeElement, StitchGenerator> bindingMap = new LinkedHashMap<>();
        while (!entries.isEmpty()) {
            Map.Entry<TypeElement, StitchGenerator.Builder> entry = entries.removeFirst();
            TypeElement typeElement = entry.getKey();
            StitchGenerator.Builder builder = entry.getValue();
            bindingMap.put(typeElement, builder.build());
        }
        return bindingMap;
    }

    private void parseAnyThreadAnnotation(Element element, Map<TypeElement, Builder> builderMap) {
        if (!(element instanceof ExecutableElement) || element.getKind() != METHOD) {
            throw new IllegalStateException(
                    String.format("@%s annotation must be on a method.",
                            CallOnAnyThread.class.getSimpleName()));
        }

        ExecutableElement executableElement = (ExecutableElement) element;
        if (!executableElement.getModifiers().contains(Modifier.PUBLIC)) {
            throw new IllegalStateException(
                    String.format("@%s annotation must be on a public method.",
                            CallOnAnyThread.class.getSimpleName()));
        }
        TypeElement typeElement = (TypeElement) element.getEnclosingElement();
        String name = executableElement.getSimpleName().toString();

        List<? extends VariableElement> methodParameters = executableElement.getParameters();
        TypeMirror returnTypeMirror = executableElement.getReturnType();

        MethodParameter[] parameters = MethodParameter.NONE;

        if (!methodParameters.isEmpty()) {
            parameters = new MethodParameter[methodParameters.size()];
            for (int i = 0; i < methodParameters.size(); i++) {
                VariableElement variableElement = methodParameters.get(i);
                TypeMirror methodParameterType = variableElement.asType();
                if (methodParameterType instanceof TypeVariable) {
                    TypeVariable typeVariable = (TypeVariable) methodParameterType;
                    methodParameterType = typeVariable.getUpperBound();
                }
                parameters[i] = new MethodParameter(i, TypeName.get(methodParameterType),
                        variableElement.getSimpleName().toString());
            }
        }

        MethodThreadStitching methodThreadStitching = new MethodThreadStitching(
                name,
                TypeName.get(returnTypeMirror),
                Arrays.asList(parameters),
                Constants.TYPE_ANY_THREAD);

        StitchGenerator.Builder builder = getOrCreateStitchingBuilder(builderMap, typeElement);
        builder.addMethod(methodThreadStitching);

    }

    private void parseNewThreadAnnotation(Element element, Map<TypeElement, Builder> builderMap) {
        if (!(element instanceof ExecutableElement) || element.getKind() != METHOD) {
            throw new IllegalStateException(
                    String.format("@%s annotation must be on a method.",
                            CallOnNewThread.class.getSimpleName()));
        }

        ExecutableElement executableElement = (ExecutableElement) element;
        if (!executableElement.getModifiers().contains(Modifier.PUBLIC)) {
            throw new IllegalStateException(
                    String.format("@%s annotation must be on a public method.",
                            CallOnNewThread.class.getSimpleName()));
        }
        TypeElement typeElement = (TypeElement) element.getEnclosingElement();
        String name = executableElement.getSimpleName().toString();

        List<? extends VariableElement> methodParameters = executableElement.getParameters();
        TypeMirror returnTypeMirror = executableElement.getReturnType();

        MethodParameter[] parameters = MethodParameter.NONE;

        if (!methodParameters.isEmpty()) {
            parameters = new MethodParameter[methodParameters.size()];
            for (int i = 0; i < methodParameters.size(); i++) {
                VariableElement variableElement = methodParameters.get(i);
                TypeMirror methodParameterType = variableElement.asType();
                if (methodParameterType instanceof TypeVariable) {
                    TypeVariable typeVariable = (TypeVariable) methodParameterType;
                    methodParameterType = typeVariable.getUpperBound();
                }
                parameters[i] = new MethodParameter(i, TypeName.get(methodParameterType),
                        variableElement.getSimpleName().toString());
            }
        }

        MethodThreadStitching methodThreadStitching = new MethodThreadStitching(
                name,
                TypeName.get(returnTypeMirror),
                Arrays.asList(parameters),
                Constants.TYPE_NEW_THREAD);

        StitchGenerator.Builder builder = getOrCreateStitchingBuilder(builderMap, typeElement);
        builder.addMethod(methodThreadStitching);
    }

    private void parseUiThreadAnnotation(Element element, Map<TypeElement, Builder> builderMap) {
        if (!(element instanceof ExecutableElement) || element.getKind() != METHOD) {
            throw new IllegalStateException(
                    String.format("@%s annotation must be on a method.",
                            CallOnUiThread.class.getSimpleName()));
        }

        ExecutableElement executableElement = (ExecutableElement) element;
        if (!executableElement.getModifiers().contains(Modifier.PUBLIC)) {
            throw new IllegalStateException(
                    String.format("@%s annotation must be on a public method.",
                            CallOnUiThread.class.getSimpleName()));
        }
        TypeElement typeElement = (TypeElement) element.getEnclosingElement();
        String name = executableElement.getSimpleName().toString();

        List<? extends VariableElement> methodParameters = executableElement.getParameters();
        TypeMirror returnTypeMirror = executableElement.getReturnType();
        if (returnTypeMirror.getKind() != TypeKind.VOID) {
            throw new IllegalStateException(String.format("@%s annotation must be on void method.",
                    CallOnUiThread.class.getSimpleName()));
        }

        MethodParameter[] parameters = MethodParameter.NONE;

        if (!methodParameters.isEmpty()) {
            parameters = new MethodParameter[methodParameters.size()];
            for (int i = 0; i < methodParameters.size(); i++) {
                VariableElement variableElement = methodParameters.get(i);
                TypeMirror methodParameterType = variableElement.asType();
                if (methodParameterType instanceof TypeVariable) {
                    TypeVariable typeVariable = (TypeVariable) methodParameterType;
                    methodParameterType = typeVariable.getUpperBound();
                }
                parameters[i] = new MethodParameter(i, TypeName.get(methodParameterType),
                        variableElement.getSimpleName().toString());
            }
        }

        MethodThreadStitching methodThreadStitching = new MethodThreadStitching(name,
                TypeName.get(returnTypeMirror),
                Arrays.asList(parameters),
                Constants.TYPE_UI_THREAD);
        StitchGenerator.Builder builder = getOrCreateStitchingBuilder(builderMap, typeElement);
        builder.addMethod(methodThreadStitching);
    }

    private StitchGenerator.Builder getOrCreateStitchingBuilder(
            Map<TypeElement, StitchGenerator.Builder> builderMap, TypeElement enclosingElement) {
        StitchGenerator.Builder builder = builderMap.get(enclosingElement);
        if (builder == null) {
            builder = StitchGenerator.newBuilder(enclosingElement);
            builderMap.put(enclosingElement, builder);
        }
        return builder;
    }
}
