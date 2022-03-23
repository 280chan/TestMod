package testmod.potions;

import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.localization.PotionStrings;
import com.megacrit.cardcrawl.potions.AbstractPotion;

import testmod.utils.MiscMethods;

public abstract class AbstractTestPotion extends AbstractPotion implements MiscMethods {
	protected static PotionStrings Strings(String ID) {
		return CardCrawlGame.languagePack.getPotionString(ID);
	}
	
	public AbstractTestPotion(String name, String id, PotionRarity rarity, PotionSize size, PotionColor color) {
		super(name, id, rarity, size, color);
	}

	protected abstract String getDesc();
	
	public AbstractPotion makeCopy() {
		try {
			return this.getClass().newInstance();
		} catch (IllegalAccessException | InstantiationException var2) {
			throw new RuntimeException("Failed to auto-generate makeCopy for potion: " + this.ID);
		}
	}
	
	public void initializeData() {
		this.potency = getPotency();
		this.description = getDesc();
		this.tips.clear();
		this.tips.add(new PowerTip(this.name, this.description));
	}
}