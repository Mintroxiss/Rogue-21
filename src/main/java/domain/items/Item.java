package domain.items;

import java.util.Objects;

public class Item {
    private final ItemType type;
    private final ItemSubtype subtype;
    private final Integer healthBoost;
    private final Integer maxHealthBoost;
    private final Integer agilityBoost;
    private final Integer strengthBoost;
    private final Integer price;

    public Item(
            ItemType type,
            ItemSubtype subtype,
            Integer healthBoost,
            Integer maxHealthBoost,
            Integer agilityBoost,
            Integer strengthBoost,
            Integer price
    ) {
        this.type = type;
        this.subtype = subtype;
        this.healthBoost = healthBoost;
        this.maxHealthBoost = maxHealthBoost;
        this.agilityBoost = agilityBoost;
        this.strengthBoost = strengthBoost;
        this.price = price;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        Item item = (Item) obj;
        return type == item.type && subtype == item.subtype &&
                healthBoost.equals(item.healthBoost) &&
                maxHealthBoost.equals(item.maxHealthBoost) &&
                agilityBoost.equals(item.agilityBoost) &&
                strengthBoost.equals(item.strengthBoost) &&
                price.equals(item.price);
    }

    public ItemType getType() {
        return type;
    }

    public ItemSubtype getSubtype() {
        return subtype;
    }

    public Integer getHealthBoost() {
        return healthBoost;
    }

    public Integer getMaxHealthBoost() {
        return maxHealthBoost;
    }

    public Integer getAgilityBoost() {
        return agilityBoost;
    }

    public Integer getStrengthBoost() {
        return strengthBoost;
    }

    public Integer getPrice() {
        return price;
    }
}