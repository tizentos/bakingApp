package ltd.boku.bakingapp.model;

import java.io.Serializable;

public class Ingredient implements Serializable {
    private static final long serialVersionUID=1L;

    private int quantity;
    private Measure measure;
    private String ingredient;

    public Ingredient(int quantity, Measure measure, String ingredient) {
        this.quantity = quantity;
        this.measure = measure;
        this.ingredient = ingredient;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Measure getMeasure() {
        return measure;
    }

    public void setMeasure(Measure measure) {
        this.measure = measure;
    }

    public String getIngredient() {
        return ingredient;
    }

    public void setIngredient(String ingredient) {
        this.ingredient = ingredient;
    }
}
