package domain.creatures;

import domain.positions.MovablePosition;
import domain.items.Item;
import domain.items.ItemType;

public class Character extends Creature{
    private Integer maxHealth = 12;
    private Item currentWeapon;

    public Character(Item currentWeapon, MovablePosition pos) {
        super(12, 5, 16, pos);
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
