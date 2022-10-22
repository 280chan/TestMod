package testmod.relicsup;

import java.util.stream.Stream;

import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.InvisiblePower;
import com.evacipated.cardcrawl.mod.stslib.relics.ClickableRelic;
import com.megacrit.cardcrawl.actions.common.LoseHPAction;
import com.megacrit.cardcrawl.actions.watcher.ChooseOneAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import testmod.cards.AbstractTestCard;
import testmod.mymod.TestMod;
import testmod.powers.AbstractTestPower;
import testmod.relics.PhasePocketWatch;

public class PhasePocketWatchUp extends AbstractUpgradedRelic implements ClickableRelic {
	private boolean enemyTurn = false;
	private static int mode = 0;
	
	public static void load(int load) {
		mode = load;
	}
	
	private static void save() {
		TestMod.save(PhasePocketWatch.SAVE_NAME, mode);
	}
	
	public void onEquip() {
		TestMod.setActivity(this);
		if (this.isActive) {
			load(TestMod.getInt(PhasePocketWatch.SAVE_NAME));
		}
	}
	
	public String getUpdatedDescription() {
		return p() == null ? DESCRIPTIONS[0] : (DESCRIPTIONS[0] + DESCRIPTIONS[1] + DESCRIPTIONS[mode + 2]);
	}
	
	public void atPreBattle() {
		if (!this.isActive)
			return;
		this.counter = 0;
		p().powers.add(new PhasePocketWatchPowerUp());
	}
	
	public void atTurnStart() {
		enemyTurn = false;
		if (this.counter < 4)
			return;
		if (mode == 0) {
			this.addTmpActionToBot(() -> {
				InputHelper.moveCursorToNeutralPosition();
				atb(new ChooseOneAction(Stream.of(1, 2)
						.map(i -> new PhasePocketWatchOptionCardUp(i, this.counter / 2 - 1)).collect(toArrayList())));
			});
		} else {
			int dmg = mode == 1 ? 2 : this.counter / 2 - 1;
			int size = mode == 1 ? this.counter / 2 - 1: 2;
			this.getIdenticalList(dmg, size).forEach(i -> this.atb(new LoseHPAction(p(), p(), i)));
			this.counter = 0;
		}
	}
	
	public void onPlayerEndTurn() {
		enemyTurn = true;
	}
	
	public void onVictory() {
		this.counter = -1;
		this.enemyTurn = false;
	}
	
	private class PhasePocketWatchPowerUp extends AbstractTestPower implements InvisiblePower {
		public PhasePocketWatchPowerUp() {
			this.owner = p();
			this.addMapWithSkip(p -> new PhasePocketWatchPowerUp());
		}
		public int onLoseHp(int damage) {
			if (enemyTurn && damage > 0) {
				show();
				counter += damage;
			}
			return enemyTurn ? 0 : damage;
		}
	}

	private class PhasePocketWatchOptionCardUp extends AbstractTestCard {
		private static final String thisID = "PhasePocketWatchOptionCardUp";

		public PhasePocketWatchOptionCardUp(int choice, int dmg) {
			super(thisID, Strings(thisID).NAME + Strings(thisID).EXTENDED_DESCRIPTION[choice + 2], -2, "",
					CardType.STATUS, CardRarity.COMMON, CardTarget.NONE);
			this.magicNumber = this.baseMagicNumber = dmg;
			this.misc = choice;
			int d = this.misc == 1 ? 2 : this.magicNumber;
			int size = this.misc == 1 ? this.magicNumber : 2;
			this.rawDescription = this.exDesc()[0] + d + this.exDesc()[1] + size + this.exDesc()[2];
			this.initializeDescription();
		}
		@Override
		public void upgrade() {}
		@Override
		public void use(AbstractPlayer arg0, AbstractMonster arg1) {}
		public void onChoseThisOption() {
			int dmg = this.misc == 1 ? 2 : this.magicNumber;
			int size = this.misc == 1 ? this.magicNumber : 2;
			this.getIdenticalList(dmg, size).forEach(i -> this.atb(new LoseHPAction(p(), p(), i)));
			counter = 0;
		}
		public AbstractCard makeCopy() {
			return new PhasePocketWatchOptionCardUp(this.misc, this.baseMagicNumber);
		}
	}
	
	@Override
	public void onRightClick() {
		if (this.isActive && !enemyTurn) {
			mode++;
			mode %= 3;
			save();
			this.updateDescription();
		}
	}

}