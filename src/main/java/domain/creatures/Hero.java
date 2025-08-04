package domain.creatures;

import domain.TileType;
import domain.positions.MovablePosition;
import domain.items.Item;
import domain.items.ItemType;

public class Hero extends Creature {
    private Integer maxHealth = 12;
    private Item currentWeapon;

    public Hero(MovablePosition pos, Item currentWeapon) {
        super(12, 5, 16, pos, TileType.HERO);
        this.currentWeapon = currentWeapon;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public void setMaxHealth(Integer maxHealth) {
        this.maxHealth = maxHealth;
    }

    public void changeWeapon(Item weapon) {
        if (weapon.getType() == ItemType.WEAPON) {
            Item weaponCopy = currentWeapon;
            currentWeapon = weapon;
        }
    }
}
