package calculator;

import java.awt.Color;
import java.awt.event.ActionListener;

public enum Buttons {
    PARENTHESES("Parentheses", "()", true, Color.WHITE, new OperatorAction()),
    EMPTY("Empty", "", false, Color.WHITE, new EditAction()),
    CLEAR("Clear", "C", true, Color.LIGHT_GRAY, new EditAction()),
    DELETE("Delete", OperatorConstants.DELETE, true, Color.LIGHT_GRAY, new EditAction()),
    SQ_ROOT("SquareRoot", OperatorConstants.SQ_ROOT, true, Color.LIGHT_GRAY, new OperatorAction()),
    SQUARED("PowerTwo", "X\u00B2", true, Color.LIGHT_GRAY, new OperatorAction()),
    POWER("PowerY", "Xy", true, Color.LIGHT_GRAY, new OperatorAction()),
    DIVIDE("Divide", OperatorConstants.DIVIDE, true, Color.LIGHT_GRAY, new OperatorAction()),
    SEVEN("Seven", "7", true, Color.WHITE, new OperandAction()),
    EIGHT("Eight", "8", true, Color.WHITE, new OperandAction()),
    NINE("Nine", "9", true, Color.WHITE, new OperandAction()),
    MULTIPLY("Multiply", OperatorConstants.MULTIPLY, true, Color.LIGHT_GRAY, new OperatorAction()),
    FOUR("Four", "4", true, Color.WHITE, new OperandAction()),
    FIVE("Five", "5", true, Color.WHITE, new OperandAction()),
    SIX("Six", "6", true, Color.WHITE, new OperandAction()),
    SUBTRACT("Subtract", OperatorConstants.MINUS, true, Color.LIGHT_GRAY, new OperatorAction()),
    ONE("One", "1", true, Color.WHITE, new OperandAction()),
    TWO("Two", "2", true, Color.WHITE, new OperandAction()),
    THREE("Three", "3", true, Color.WHITE, new OperandAction()),
    ADD("Add", OperatorConstants.PLUS, true, Color.LIGHT_GRAY, new OperatorAction()),
    PLUS_MINUS("PlusMinus", OperatorConstants.PLUS_MINUS, true, Color.LIGHT_GRAY, new OperatorAction()),
    ZERO("Zero", "0", true, Color.WHITE, new OperandAction()),
    DOT("Dot", ".", true, Color.WHITE, new OperandAction()),
    EQUALS("Equals", "=", true, Color.LIGHT_GRAY, new EditAction());

    private final String name;
    private final String text;
    private final boolean isVisible;
    private final Color color;
    private final ActionListener listener;

    Buttons(String name, String text, boolean isVisible, Color color, ActionListener listener) {
        this.name = name;
        this.text = text;
        this.isVisible = isVisible;
        this.color = color;
        this.listener = listener;
    }

    public String getName() {
        return name;
    }

    public String getText() {
        return text;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public Color getColor() {
        return color;
    }

    public ActionListener getListener() {
        return listener;
    }
}