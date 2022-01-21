package relics;

import java.util.stream.Stream;

import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.InvisiblePower;
import com.evacipated.cardcrawl.mod.stslib.relics.ClickableRelic;
import com.megacrit.cardcrawl.actions.common.LoseHPAction;
import com.megacrit.cardcrawl.actions.watcher.ChooseOneAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import cards.AbstractTestCard;
import mymod.TestMod;
import powers.AbstractTestPower;

public class PhasePocketWatch extends AbstractTestRelic implements ClickableRelic {
	public static final String SAVE_NAME = "PhasePocketWatch";
	private boolean enemyTurn = false;
	private static int mode = 0;
	
	public static void load(int load) {
		mode = load;
	}
	
	private static void save() {
		TestMod.save(SAVE_NAME, mode);
	}
	
	public void onEquip() {
		TestMod.setActivity(this);
		if (this.isActive)
			save();
	}
	
	public void onUnequip() {
		if (this.isActive) {
			mode = 0;
			save();
		}
	}
	
	public PhasePocketWatch() {
		super(RelicTier.RARE, LandingSound.MAGICAL);
	}
	
	public String getUpdatedDescription() {
		return p() == null ? DESCRIPTIONS[0] : (DESCRIPTIONS[0] + DESCRIPTIONS[1] + DESCRIPTIONS[mode + 2]);
	}
	
	public void atPreBattle() {
		if (!this.isActive)
			return;
		this.counter = 0;
		p().powers.add(new PhasePocketWatchPower());
    }
	
	public void atTurnStart() {
		enemyTurn = false;
		if (this.counter <= 0)
			return;
		if (mode == 0) {
			this.addTmpActionToBot(() -> {
				InputHelper.moveCursorToNeutralPosition();
				atb(new ChooseOneAction(Stream.of(1, 2).map(i -> new PhasePocketWatchOptionCard(i, this.counter / 2))
						.collect(toArrayList())));
			});
		} else {
			int dmg = mode == 1 ? 2 : this.counter / 2;
			int size = mode == 1 ? this.counter / 2 : 2;
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
	
	private class PhasePocketWatchPower extends AbstractTestPower implements InvisiblePower {
		public PhasePocketWatchPower() {
			super(SAVE_NAME);
			this.owner = p();
		}
		public int onLoseHp(int damage) {
			if (enemyTurn && damage > 0) {
				show();
				counter += damage;
			}
			return enemyTurn ? 0 : damage;
		}
		public void onRemove() {
			this.addTmpActionToTop(() -> this.owner.powers.add(new PhasePocketWatchPower()));
		}
	}

	private class PhasePocketWatchOptionCard extends AbstractTestCard {
		private static final String thisID = "PhasePocketWatchOptionCard";

		public PhasePocketWatchOptionCard(int choice, int dmg) {
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