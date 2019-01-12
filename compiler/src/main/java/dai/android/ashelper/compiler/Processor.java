package dai.android.ashelper.compiler;

import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.Arrays;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

import dai.android.ashelper.annotation.WorkModule;

import static dai.android.ashelper.annotation.Constants.PACKAGE_OF_GENERATE;
import static dai.android.ashelper.annotation.Constants.PRE_CLASS_NAME;


public class Processor extends AbstractProcessor {

    private Logger mLogger;
    private Filer mFiler;

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latest();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return new TreeSet<>(Arrays.asList(
                WorkModule.class.getCanonicalName()
        ));
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);

        mLogger = new Logger(processingEnvironment.getMessager());
        mFiler = processingEnvironment.getFiler();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set,
                           RoundEnvironment roundEnvironment) {
        if (null == set || set.isEmpty()) {
            return false;
        }

        Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(WorkModule.class);
        if (null == elements || elements.isEmpty()) {
            return true;
        }

        mLogger.info(
                String.format(Locale.CHINA, "Analytic annotation: '%s', with %d elements.",
                        WorkModule.class.getSimpleName(), elements.size()
                )
        );

        for (Element element : elements) {
            try {
                verifyModule(element);
            } catch (ProcessException e) {
                mLogger.warning(e.getMessage());
                continue;
            }

            WorkModule m = element.getAnnotation(WorkModule.class);
            mLogger.info("element: " + element.toString());
            if (!m.canWork()) {
                mLogger.info("\tdisabled this module by " + m.author());
                continue;
            }

            try {
                generateCode(element);
            } catch (ProcessException e) {
                mLogger.error(e.getMessage());
            }
        }

        return true;
    }


    private void generateCode(Element element) throws ProcessException {
        if (null == element) {
            mLogger.warning("this is null element, abandon to generate code");
            return;
        }

        String className = PRE_CLASS_NAME + Utility.md5(element.toString());
        mLogger.info("\tauto generate class name: " + className);

        TypeSpec.Builder builder =
                TypeSpec.classBuilder(className)
                        .addModifiers(Modifier.FINAL, Modifier.PUBLIC);
        FieldSpec clazz = FieldSpec.builder(String.class, "sClass")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                .initializer("\"" + element.toString() + "\"")
                .build();
        builder.addField(clazz);

        TypeSpec typeSpec = builder.build();
        JavaFile javaFile = JavaFile.builder(PACKAGE_OF_GENERATE, typeSpec).build();
        try {
            javaFile.writeTo(mFiler);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void verifyModule(Element moduleElement) throws ProcessException {
        WorkModule m = moduleElement.getAnnotation(WorkModule.class);
        if (null == m) {
            throw new ProcessException(
                    String.format("Current element(%s) not has '%s' any annotation.",
                            moduleElement.toString(),
                            WorkModule.class.getSimpleName()
                    )
            );
        }

        if (ElementKind.CLASS != moduleElement.getKind()) {
            throw new ProcessException(
                    String.format("'%s' not a class, just class can use '%s'",
                            moduleElement.toString(), WorkModule.class.getSimpleName()));
        }

        TypeElement classElement = (TypeElement) moduleElement;
        Set<Modifier> modifiers = classElement.getModifiers();
        if (!modifiers.contains(Modifier.PUBLIC)) {
            throw new ProcessException(
                    String.format("'%s' permission modifier must be public",
                            classElement.getQualifiedName().toString())
            );
        }

        if (modifiers.contains(Modifier.ABSTRACT)) {
            throw new ProcessException(
                    String.format("'%s' is abstract class, can not use at '%s'",
                            classElement.getQualifiedName().toString(),
                            WorkModule.class.getSimpleName()
                    )
            );
        }
    }

}
