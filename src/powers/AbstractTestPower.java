package powers;

import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;

import mymod.TestMod;

public abstract class AbstractTestPower extends AbstractPower {
	protected static PowerStrings Strings(String shortID) {
		return CardCrawlGame.languagePack.getPowerStrings(TestMod.makeID(shortID));
	}
	
	private String IMGPath(String shortID) {
		return TestMod.powerIMGPath(shortID);
	}

	public AbstractTestPower(String shortID) {
		this.ID = TestMod.makeID(shortID);
		this.img = ImageMaster.loadImage(this.IMGPath(shortID));
	}
	
	public AbstractTestPower(String shortID, String region) {
		this.ID = TestMod.makeID(shortID);
		this.loadRegion(region);
	}
	
	public void stackPower(int stackAmount) {
		this.fontScale = 8.0f;
        this.amount += stackAmount;
	}
}
