package potions;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.potions.AbstractPotion;

import mymod.TestMod;
import utils.MiscMethods;

public class TimePotion extends AbstractPotion implements MiscMethods {
	public static final String POTION_ID = TestMod.makeID("TimePotion");
	public static final String NAME = "时间女神的恶作剧";
	public static final String[] DESCRIPTIONS = {"立即结束你的回合，敌人在这回合不会执行其意图，然后开始你的下一回合。"};

	public TimePotion() {
		super(NAME, POTION_ID, PotionRarity.RARE, PotionSize.GHOST, PotionColor.SMOKE);
		this.potency = getPotency();
		this.description = this.getDesc();
		this.isThrown = true;
		this.tips.add(new PowerTip(this.name, this.description));
	}

	public String getDesc() {
		return DESCRIPTIONS[0];
	}

	public void use(AbstractCreature target) {
		this.turnSkipperStart();
	}

	public AbstractPotion makeCopy() {
		return new TimePotion();
	}

	public int getPotency(int ascensionLevel) {
		return 0;
	}
}
