package domain.items;

public class Item {
    private final ItemType type;
    private final ItemSubtype subtype;
    private Integer healthBoost = null;
    private Integer maxHealthBoost = null;
    private Integer agilityBoost = null;
    private Integer strengthBoost = null;
    private Integer price = null;
    private int count = 0;

    public Item(
            ItemType type,
            ItemSubtype subtype,
            int count
    ) {
        this.type = type;
        this.subtype = subtype;
        switch (type) {
            case WEAPON -> this.strengthBoost = subtype.getValue();
            case ARMOR -> this.agilityBoost = subtype.getValue();
            case FOOD -> this.healthBoost = subtype.getValue();
            case SCROLL, POTION -> {
                switch (subtype) {
                    case SCROLL_OF_DEXTERITY, POTION_OF_DEXTERITY -> this.agilityBoost = subtype.getValue();
                    case SCROLL_OF_VITALITY, POTION_OF_VITALITY -> this.maxHealthBoost = subtype.getValue();
                    case SCROLL_OF_STRENGTH, POTION_OF_STRENGTH -> this.strengthBoost = subtype.getValue();
                    default -> throw new IllegalArgumentException("ItemSubtype is not processed");
                }
            }
            case TREASURE -> this.price = subtype.getValue();
            default -> throw new IllegalArgumentException("ItemType is not processed");
        }
        if (count <= 0) {
            throw new IllegalArgumentException("Count of item does not be <= 0");
        }
        this.count = count;
    }

    public Item(Item item, int count) {
        this(item.getType(), item.getSubtype(), count);
    }

    /**
     * Возвращает описание предмета
     *
     * @return информация о предмете
     */
    public String getDescription() {
        String res = count + " +";
        res += switch (type) {
            case WEAPON -> strengthBoost;
            case ARMOR -> agilityBoost;
            case FOOD -> healthBoost;
            case TREASURE -> price;
            case SCROLL, POTION -> switch (subtype) {
                case SCROLL_OF_DEXTERITY, POTION_OF_DEXTERITY -> agilityBoost;
                case SCROLL_OF_VITALITY, POTION_OF_VITALITY -> maxHealthBoost;
                case SCROLL_OF_STRENGTH, POTION_OF_STRENGTH -> strengthBoost;
                default -> 0;
            };
        };
        res += " " + subtype.getName();
        return res;
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
        return item.getType() == type && item.getSubtype() == subtype;
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

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}