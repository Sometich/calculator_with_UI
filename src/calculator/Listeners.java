package calculator;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class Listeners extends AbstractAction {
    Function functions = new Function();

}

class OperandAction extends Listeners {

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        functions.appendEquation(actionEvent.getActionCommand());
    }
}

class EditAction extends Listeners {
    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        String pressed = actionEvent.getActionCommand();

        if (pressed.equals("C")) {
            functions.setEquation("");
        }

        if (pressed.equals(OperatorConstants.DELETE)) {
            functions.deleteEquationLastChar();
        }

        if (pressed.equals("=")) {
            functions.parseEquation();
        }
    }
}

class OperatorAction extends Listeners {
    private final Pattern endsWithOperator = Pattern.compile(".*" + OperatorConstants.OPS_OR_PAREN + "$");

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        JButton button = (JButton) actionEvent.getSource();
        String buttonText = button.getText();
        String buttonName = button.getName();

        if (buttonText.matches(OperatorConstants.OPERATORS)) {
            appendOperator(buttonText);
            return;
        }

        if (buttonName.equals(Buttons.PARENTHESES.getName())) {
            addParentheses();
            return;
        }

        if (buttonName.equals(Buttons.SQ_ROOT.getName())) {
            addSquareRoot();
            return;
        }

        if (buttonName.equals(Buttons.POWER.getName()) || buttonName.equals(Buttons.SQUARED.getName())) {
            addExponentiation(buttonName);
        }

        if (buttonName.equals(Buttons.PLUS_MINUS.getName())) {
            addNegation();
        }
    }

    private void appendOperator(String buttonText) {
        String equation = functions.getEquation();

        if (equation.isEmpty()) {   // nothing to do if the equation is blank
            return;
        }

        if (functions.getLastCharOfEquation().matches(OperatorConstants.OPERATORS)) {
            functions.deleteEquationLastChar();
        }

        functions.appendEquation(buttonText);
        functions.fixDanglingPeriods();
    }

    private void addSquareRoot() {
        String equation = functions.getEquation();
        if (equation.isEmpty() || endsWithOperator.matcher(equation).find()) {
            functions.appendEquation(OperatorConstants.SQ_ROOT + "(");
        }
    }

    /**
     * Add a parenthesis to the equation. Determine which to add:
     * 1. Left parenthesis if number of left and right parentheses are equal and has an operator preceding it.
     * 2. Left parenthesis if last character of equation is a left parenthesis or another operator
     * 3. Right parenthesis if neither of the first two conditions are met.
     *
     * Only add the parenthesis at the beginning of an equation, if the preceding
     */
    private void addParentheses() {
        String equation = functions.getEquation();
        String lastChar = functions.getLastCharOfEquation();
        long left = equation.chars().filter(ch -> ch == '(').count();
        long right = equation.chars().filter(ch -> ch == ')').count();
        if (equation.isEmpty() || lastChar.equals("(") ||
                left == right || lastChar.matches(OperatorConstants.OPERATORS)) {
            functions.appendEquation("(");
        } else {
            functions.appendEquation(")");
        }
    }

    /**
     * Add exponentiation to the equation.
     * <p>
     * Exponentiation is added to the equation only if the last character of the equation is a digit. The correct
     * exponent is added if the square button was pressed, else we just add the opening parenthesis so the user can
     * enter the power they desire.
     * @param type name of the button that was pressed
     */
    private void addExponentiation(String type) {
        if (functions.getLastCharOfEquation().matches("[\\d]")) {
            if (type.equals(Buttons.SQUARED.getName())) {
                functions.appendEquation(OperatorConstants.POWER + "(2)");
            }

            if (type.equals(Buttons.POWER.getName())) {
                functions.appendEquation(OperatorConstants.POWER + "(");
            }
        }
    }

    /**
     * Add negation (<code>(-</code>) to the equation.
     * <p>
     * Adds negation to the equation if the equation is empty or the last character is an operator.
     * If the user presses the negation button in succession, each even press removes the current negation, and each
     * odd press adds it back - effectively acting as a toggle instead of adding multiple instances of <code>(-</code>.
     */
    private void addNegation() {
        String equation = functions.getEquation();

        if (equation.endsWith("(-")) {
            functions.deleteEquationLastChar();
            functions.deleteEquationLastChar();
            return;
        }

        if (equation.isEmpty() || endsWithOperator.matcher(equation).find()) {
            functions.appendEquation("(-");
            return;
        }

        Matcher endsWithOperand = Pattern.compile("(?<operand>[0-9]*\\.?[0-9]+)$").matcher(equation);
        if (endsWithOperand.find()) {
            int start = endsWithOperand.start();
            int startLess2 = Math.max(start - 2, 0);
            if (equation.startsWith("(-", startLess2)) {
                functions.setEquation(equation.substring(0, startLess2) + endsWithOperand.group());
            } else {
                functions.setEquation(equation.substring(0, start) + "(-" + endsWithOperand.group());
            }
        }
    }
}