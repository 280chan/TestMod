package potions;

import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.localization.PotionStrings;
import com.megacrit.cardcrawl.potions.AbstractPotion;

public abstract class AbstractTestPotion extends AbstractPotion {

	protected static PotionStrings Strings(String ID) {
		return CardCrawlGame.languagePack.getPotionString(ID);
	}
	
	public AbstractTestPotion(String name, String id, PotionRarity rarity, PotionSize size, PotionColor color) {
		super(name, id, rarity, size, color);
		this.potency = this.getPotency();
		this.description = this.getDesc();
		this.tips.clear();
		this.tips.add(new PowerTip(this.name, this.description));
	}

	protected abstract String getDesc();
	
}
