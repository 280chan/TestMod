package deprecated.relics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer.PlayerClass;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.AttackBurnPower;
import com.megacrit.cardcrawl.powers.ConservePower;
import com.megacrit.cardcrawl.powers.ForcefieldPower;
import com.megacrit.cardcrawl.powers.GrowthPower;
import com.megacrit.cardcrawl.powers.SkillBurnPower;
import com.megacrit.cardcrawl.powers.TimeMazePower;

import mymod.TestMod;
import relics.AbstractTestRelic;

/**
 * @deprecated
 */
public class HistoricalDocuments extends AbstractTestRelic {
	public static final String ID = "HistoricalDocuments";
	public static final String IMG = TestMod.cardIMGPath("relic1");
	public static final String DESCRIPTION = "每回合开始获得一种随机的被尖塔废弃的正面或负面状态。";
	
	public HistoricalDocuments() {
		super(ID, RelicTier.RARE, LandingSound.MAGICAL);
	}
	
	public String getUpdatedDescription() {
		return DESCRIPTIONS[0];
	}

	public void updateDescription(PlayerClass c) {
		this.tips.clear();
	    this.tips.add(new PowerTip(this.name, this.getUpdatedDescription()));
	    initializeTips();
	}
	
	private AbstractPower randomHistoricalPower(AbstractCreature owner) {
		AbstractPower p;
		int r = (int) (Math.random() * 6);
		switch (r) {
		case 0:
			p = new AttackBurnPower(owner, 1);
			break;
		case 1:
			p = new GrowthPower(owner, 1);
			break;
		case 2:
			p = new SkillBurnPower(owner, 1);
			break;
		case 3:
			p = new ConservePower(owner, 1);
			break;
		case 4:
			p = new ForcefieldPower(owner);
			break;
		case 5:
			p = new TimeMazePower(owner, 15);
			break;
		default:
			p = null;
			System.out.println("随机状态越界wtf?");
		}
		if (r < 3) {
			p.atEndOfRound();
		}
		return p;
	}
	
	private void applyRandomHistoricalPower(AbstractCreature owner) {
		AbstractPower tmp = randomHistoricalPower(owner);
		if (tmp == null)
			return;
		int stack = tmp.amount;
		if (stack == -1 || stack == 15)
			stack = 0;
		AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(owner, owner, tmp, stack));
	}
	
	public void atTurnStart() {
		applyRandomHistoricalPower(AbstractDungeon.player);
		this.show();
    }
	
}