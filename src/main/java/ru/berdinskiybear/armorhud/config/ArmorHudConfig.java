package ru.berdinskiybear.armorhud.config;

public class ArmorHudConfig {

    private Anchor anchor;
    private Side side;
    private int offsetX;
    private int offsetY;
    private boolean fullWidgetShown;
    private OffhandSlotBehavior offhandSlotBehavior;
    private boolean reversed;

    public ArmorHudConfig() {
        this.anchor = Anchor.HOTBAR;
        this.side = Side.LEFT;
        this.offsetX = 0;
        this.offsetY = 0;
        this.fullWidgetShown = false;
        this.offhandSlotBehavior = OffhandSlotBehavior.ADHERE;
        this.reversed = true;
    }

    public static ArmorHudConfig readConfig() {
        return new ArmorHudConfig();
        //TODO actual reading
    }

    public Anchor getAnchor() {
        return anchor;
    }

    public void setAnchor(Anchor anchor) {
        this.anchor = anchor;
    }

    public Side getSide() {
        return side;
    }

    public void setSide(Side side) {
        this.side = side;
    }

    public int getOffsetX() {
        return offsetX;
    }

    public void setOffsetX(int offsetX) {
        this.offsetX = offsetX;
    }

    public int getOffsetY() {
        return offsetY;
    }

    public void setOffsetY(int offsetY) {
        this.offsetY = offsetY;
    }

    public boolean isFullWidgetShown() {
        return fullWidgetShown;
    }

    public void setFullWidgetShown(boolean fullWidgetShown) {
        this.fullWidgetShown = fullWidgetShown;
    }

    public OffhandSlotBehavior getOffhandSlotBehavior() {
        return offhandSlotBehavior;
    }

    public void setOffhandSlotBehavior(OffhandSlotBehavior offhandSlotBehavior) {
        this.offhandSlotBehavior = offhandSlotBehavior;
    }

    public boolean isReversed() {
        return reversed;
    }

    public void setReversed(boolean reversed) {
        this.reversed = reversed;
    }

    public enum Anchor {
        TOP_CENTER,
        TOP,
        BOTTOM,
        HOTBAR;

        public boolean isTop() {
            return this == TOP || this == TOP_CENTER;
        }
    }

    public enum Side {
        RIGHT,
        LEFT
    }

    public enum OffhandSlotBehavior {
        ALWAYS_IGNORE,
        ADHERE,
        ALWAYS_LEAVE_SPACE
    }
}
