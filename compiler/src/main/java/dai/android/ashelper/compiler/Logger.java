package dai.android.ashelper.compiler;

import javax.annotation.processing.Messager;
import javax.tools.Diagnostic;

public final class Logger {
    private final Messager messager;

    public Logger(Messager messager) {
        this.messager = messager;
    }

    public void info(CharSequence info) {
        messager.printMessage(Diagnostic.Kind.NOTE, info);
    }

    public void error(CharSequence error) {
        messager.printMessage(Diagnostic.Kind.ERROR, error);
    }

    public void error(Throwable error) {
        messager.printMessage(
                Diagnostic.Kind.ERROR,
                error.getMessage() + "\n" + formatStackTrace(error.getStackTrace())
        );
    }

    public void warning(CharSequence warning) {
        messager.printMessage(Diagnostic.Kind.WARNING, warning);
    }

    private String formatStackTrace(StackTraceElement[] stackTrace) {
        StringBuilder sb = new StringBuilder();
        for (StackTraceElement element : stackTrace) {
            sb.append("    at ").append(element.toString());
            sb.append("\n");
        }
        return sb.toString();
    }
}
