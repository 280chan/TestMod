package relics;

import java.util.ArrayList;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.CombustPower;
import com.megacrit.cardcrawl.powers.PanachePower;

import mymod.TestMod;
import powers.RecapPower;


public class Register extends AbstractTestRelic{
	public static final String ID = "Register";
	
	private static final ArrayList<AbstractPower> POWERS = new ArrayList<AbstractPower>();
	
	public void clear() {
		POWERS.clear();
	}
	
	public Register() {
		super(ID, RelicTier.SHOP, LandingSound.CLINK);
	}
	
	public String getUpdatedDescription() {
		return DESCRIPTIONS[0];
	}
	
	private void updateInfo() {
		clear();
		addPowers(AbstractDungeon.player.powers);
	}
	
	private void addPowers(ArrayList<AbstractPower> powers) {
		for (AbstractPower p : powers)
			POWERS.add(p);
	}
	
	public void onEquip() {
		TestMod.setActivity(this);
		if (!isActive)
			return;
		clear();
    }
	
	public void atBattleStart() {
		if (!isActive)
			return;
		AbstractPlayer p = AbstractDungeon.player;
		boolean show = false;
		for (AbstractPower pow : POWERS) {
			if (pow.amount < 2 && pow.amount > -2)
				continue;
			else if (pow.amount < 0)
				pow.amount = -1;
			else
				pow.amount = 1;
			if (pow.owner != p)
				pow.owner = p;
			if (pow.ID.equals("Combust"))
				pow = new CombustPower(p, 1, pow.amount);//自燃损血也降为1
			else if (pow.ID.equals("Panache"))
				pow = new PanachePower(p, 10);//神气致胜伤害也降为10
			else if (pow.ID.equals("Thorns"))
				pow.stackPower(0);
			else if (pow.ID.equals("TimeMazePower"))
				pow.amount = 15;
			else if (pow instanceof RecapPower)
				((RecapPower)pow).clear();
			pow.updateDescription();
			this.addToTop(new ApplyPowerAction(p, p, pow, pow.amount));
			show = true;
		}
		if (show) {
			this.show();
		}
    }
	
	public void onMonsterDeath(final AbstractMonster m) {
		if (!isActive)
			return;
		updateInfo();
    }
	
	public boolean canSpawn() {
		return (Settings.isEndless) || (AbstractDungeon.floorNum <= 48);
	}
}