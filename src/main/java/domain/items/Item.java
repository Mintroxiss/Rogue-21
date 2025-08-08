package domain.items;

public class Item {
    private final ItemType type;
    private final ItemSubtype subtype;
    private Integer healthBoost = null;
    private Integer maxHealthBoost = null;
    private Integer agilityBoost = null;
    private Integer strengthBoost = null;
    private Integer price = null;
    private int count;
    private int value;

    public Item(
            ItemType type,
            ItemSubtype subtype,
            int value,
            int count
    ) {
        this.type = type;
        this.subtype = subtype;
        setValue(value);
        if (count <= 0) {
            throw new IllegalArgumentException("Count of item does not be <= 0");
        }
        this.count = count;
    }

    public Item(
            ItemType type,
            ItemSubtype subType
    ) {
        this(type, subType, 0, 1);
    }

    public Item(
            ItemType type,
            ItemSubtype subtype,
            int count
    ) {
        this(type, subtype, 0, count);
    }

    public Item(
            int value,
            ItemType type,
            ItemSubtype subtype
    ) {
        this(type, subtype, value, 1);
    }

    public Item(Item item) {
        this(item.getType(), item.getSubtype(), item.getValue(), item.getCount());
    }

    public Item(Item item, int count) {
        this(item.getType(), item.getSubtype(), item.getValue(), count);
    }

    public void setValue(int value) {
        this.value = value;
        switch (type) {
            case WEAPON -> this.strengthBoost = value;
            case ARMOR -> this.agilityBoost = value;
            case FOOD -> this.healthBoost = value;
            case SCROLL, POTION -> {
                switch (subtype) {
                    case SCROLL_OF_DEXTERITY, POTION_OF_DEXTERITY -> this.agilityBoost = value;
                    case SCROLL_OF_VITALITY, POTION_OF_VITALITY -> this.maxHealthBoost = value;
                    case SCROLL_OF_STRENGTH, POTION_OF_STRENGTH -> this.strengthBoost = value;
                    default -> throw new IllegalArgumentException("ItemSubtype is not processed");
                }
            }
            case TREASURE -> this.price = value;
            default -> throw new IllegalArgumentException("ItemType is not processed");
        }
    }

    /**
     * @return описание предмета
     */
    public String getDescription() {
        String res = "";
        if (count > 1) {
            res += count + " ";
        }
        res += "+" + switch (type) {
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
        } + " " + subtype.getName();
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

    public void setPrice(int price) {
        this.price = price;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public int getValue() {
        return value;
    }
}