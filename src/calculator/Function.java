package calculator;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Function {
    private static JLabel equation;
    private static JLabel result;

    private static final String OPERATORS = "(?<operator>" + OperatorConstants.OPERATORS + ")";
    private static final String OPERANDS = "(?<operand>[-+]?[0-9]*\\.?[0-9]+)";
    private static final String MODIFIERS = "(?<modifiers>\\(\\-|[\\(\\)])";
    private static final String POWERS = String.format("(?<powers>[%s%s])", OperatorConstants.SQ_ROOT, OperatorConstants.POWER);

    private static final String DIV_BY_ZERO = String.format("%1$s$|%2$s0%1$s|%2$s0$",
            OperatorConstants.OPERATORS, OperatorConstants.DIVIDE);
    private static final String DANGLING = String.format("(?<leading>^\\.|%1$s\\.\\d)|(?<trailing>\\d\\.%1$s|\\d\\.$)",
            OperatorConstants.OPERATORS);

    // ******************** GUI Instantiation ********************

    protected JPanel createDisplayPanel() {
        JPanel display = new JPanel(new FlowLayout());
        display.setBorder(new EmptyBorder(10, 10, 10, 10));
        display.add(createResultLabel());
        display.add(createEquationLabel());
        return display;
    }

    private JLabel createResultLabel() {
        result = new JLabel("0");
        result.setName("ResultLabel");
        result.setPreferredSize(new Dimension(230, 75));
        result.setMinimumSize(new Dimension(100, 75));
        result.setMaximumSize(new Dimension(500, 75));
        result.setHorizontalAlignment(SwingConstants.RIGHT);
        result.setBorder(new CompoundBorder(
                new LineBorder(Color.BLACK, 2),
                new EmptyBorder(5, 5, 5, 5)));
        result.setBackground(Color.YELLOW);
        result.setFont(new Font("MONOSPACED", Font.BOLD, 30));
        result.setOpaque(true);
        return result;
    }

    private JLabel createEquationLabel() {
        equation = new JLabel("");
        equation.setName("EquationLabel");
        equation.setPreferredSize(new Dimension(230, 30));
        equation.setHorizontalAlignment(SwingConstants.RIGHT);
        equation.setBorder(new CompoundBorder(
                new LineBorder(Color.BLACK, 1),
                new EmptyBorder(5, 5, 5, 5)));
        equation.setBackground(Color.WHITE);
        equation.setOpaque(true);
        return equation;
    }

    // *************** Equation JLabel manipulation ***************

    protected String getEquation() {
        return equation.getText();
    }

    protected void setEquation(final String text) {
        equation.setText(text);
    }

    protected void appendEquation(final String text) {
        setEquation(getEquation().concat(text));
    }

    protected void deleteEquationLastChar() {
        String text = getEquation();
        setEquation(text.substring(0, text.length() - 1));
    }

    protected String getLastCharOfEquation() {
        String text = getEquation();
        return text.length() > 0 ? text.substring(text.length() - 1) : "";
    }

    protected void setEquationToInvalid() {
        equation.setForeground(Color.RED.darker());
    }

    protected void setEquationToValid() {
        equation.setForeground(Color.BLACK);
    }

    // *************** Parsing/calculating equation ***************

    protected void parseEquation() {
        if (validateEquation()) {
            result.setText(calculateResult(infixToPostfix(getEquation()))
                    .stripTrailingZeros().toPlainString());
        }
    }

    /**
     * When equals has been pressed, validate the equation before evaluating it to ensure:
     * 1. That the equation doesn't end in an operator
     * 2. It doesn't contain a division by zero anywhere (won't catch a division by zero if zero is the result of an
     * operation however)
     */
    private boolean validateEquation() {
        boolean valid = true;
        Matcher invalid = Pattern.compile(DIV_BY_ZERO).matcher(getEquation());
        if (invalid.find()) {
            setEquationToInvalid();
            valid = false;
        } else {
            setEquationToValid();
        }

        return valid;
    }

    private String infixToPostfix(final String equation) {
        Deque<String> operatorStack = new ArrayDeque<>();
        StringBuilder expression = new StringBuilder();
        Matcher parser = Pattern.compile(OPERATORS + "|" + OPERANDS + "|" + MODIFIERS + "|" + POWERS).matcher(equation);

        // TODO: Replace string literals in group statements with String constants?
        while (parser.find()) {
            String match = parser.group();
            if (null != parser.group("operand")) {
                expression.append(match).append(" ");
            }

            if (null != parser.group("operator")) {
                while (!operatorStack.isEmpty() && precedence(match) <= precedence(operatorStack.peek())) {
                    expression.append(operatorStack.pop()).append(" ");
                }
                operatorStack.push(match);
            }

            if (null != parser.group("modifiers")) {
                if (match.equals("(-")) {
                    expression.append("0").append(" ");
                    operatorStack.push("(");
                    operatorStack.push("-");
                }

                if (match.equals("(")) {
                    operatorStack.push(match);
                }

                if (match.equals(")")) {
                    boolean matchingLeft = false;
                    while (!operatorStack.isEmpty() && !matchingLeft) {
                        if (operatorStack.peek().equals("(")) {
                            operatorStack.pop();
                            matchingLeft = true;
                        } else {
                            expression.append(operatorStack.pop()).append(" ");
                        }
                    }
                    if (!matchingLeft) {
                        expression.setLength(0);
                        setEquationToInvalid();
                    }
                }
            }

            if (null != parser.group("powers")) {
                if (match.equals(OperatorConstants.SQ_ROOT)) {
                    operatorStack.push(match);
                }

                if (match.equals(OperatorConstants.POWER)) {
                    while (!operatorStack.isEmpty() &&
                            !operatorStack.peek().matches("()") &&
                            precedence(operatorStack.peek()) > precedence(match)) {
                        expression.append(operatorStack.pop()).append(" ");
                    }
                    operatorStack.push(match);
                }
            }
        }

        while (!operatorStack.isEmpty()) {
            expression.append(operatorStack.pop()).append(" ");
        }

        return expression.toString().stripTrailing();
    }

    private int precedence(final String operator) {
        if (operator.equals(OperatorConstants.PLUS) || operator.equals(OperatorConstants.MINUS)) {
            return 1;
        }

        if (operator.equals(OperatorConstants.MULTIPLY) || operator.equals(OperatorConstants.DIVIDE)) {
            return 2;
        }

        if (operator.equals(OperatorConstants.POWER) || operator.equals(OperatorConstants.SQ_ROOT)) {
            return 3;
        }

        return -1;
    }

    private BigDecimal calculateResult(final String postfix) {
        Deque<BigDecimal> resultStack = new ArrayDeque<>();
        Pattern operator = Pattern.compile(OPERATORS);
        Pattern operands = Pattern.compile(OPERANDS);

        for (String part : postfix.split("\\s")) {
            if (operator.matcher(part).find() || part.equals(OperatorConstants.POWER)) {
                resultStack.push(doOperation(part, resultStack.pop(), resultStack.pop()));
            } else if (operands.matcher(part).find()) {
                resultStack.push(new BigDecimal(part));
            } else if (part.equals(OperatorConstants.SQ_ROOT)) {
                resultStack.push(doOperation(part, resultStack.pop(), BigDecimal.ZERO));
            }
        }

        return resultStack.pop();
    }

    private BigDecimal doOperation(String operator, BigDecimal operand1, BigDecimal operand2) {
        switch (operator) {
            case OperatorConstants.PLUS:
                return operand1.add(operand2);
            case OperatorConstants.MINUS:
                return operand2.subtract(operand1);
            case OperatorConstants.MULTIPLY:
                return operand1.multiply(operand2);
            case OperatorConstants.DIVIDE:
                return operand2.divide(operand1, MathContext.DECIMAL32);
            case OperatorConstants.SQ_ROOT:
                if (operand1.compareTo(BigDecimal.ZERO) < 0) {
                    setEquationToInvalid();
                    return BigDecimal.ZERO;
                }
                return operand1.sqrt(MathContext.DECIMAL32);
            case OperatorConstants.POWER:
                return operand2.pow(operand1.intValue(), MathContext.DECIMAL32);
            default:
                return BigDecimal.ZERO;
        }
    }

    protected void fixDanglingPeriods() {
        String text = getEquation();
        if (!(text.isEmpty())) {
            Matcher danglingPeriod = Pattern.compile(DANGLING).matcher(text);

            while (danglingPeriod.find()) {
                if (danglingPeriod.group("leading") != null) {
                    text = text.substring(0, danglingPeriod.start("leading"))
                            + danglingPeriod.group("leading").replace(".", "0.")
                            + text.substring(danglingPeriod.end("leading"));
                }

                if (danglingPeriod.group("trailing") != null) {
                    text = text.substring(0, danglingPeriod.start("trailing"))
                            + danglingPeriod.group("trailing").replace(".", ".0")
                            + text.substring(danglingPeriod.end("trailing"));
                }
            }
        }

        setEquation(text);
    }
}