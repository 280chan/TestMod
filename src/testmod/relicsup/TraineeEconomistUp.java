package testmod.relicsup;

import java.util.stream.Stream;
import com.evacipated.cardcrawl.mod.stslib.relics.ClickableRelic;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.AbstractMonster.EnemyType;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.ShopRoom;
import testmod.potions.EscapePotion;
import testmod.relics.TraineeEconomist;
import testmod.utils.CounterKeeper;
import testmod.utils.Economist;

public class TraineeEconomistUp extends AbstractUpgradedRelic implements Economist, CounterKeeper, ClickableRelic {
	private static boolean used = false;
	
	public String getUpdatedDescription() {
		return (this.counter < 1) ? DESCRIPTIONS[0] : DESCRIPTIONS[0] + DESCRIPTIONS[1] + toPercent(goldRate())
				+ DESCRIPTIONS[2] + toPercent(this.rate()) + DESCRIPTIONS[3];
	}

	private static String toPercent(double input) {
		return ((int) (input * 10000)) / TraineeEconomist.PERCENTAGE + "";
	}
	
	private double goldRate() {
		return Math.pow((100 + TraineeEconomist.DELTA_BONUS) / TraineeEconomist.PERCENTAGE, counter);
	}
	
	@Override
	public float rate() {
		return (float) Math.pow((100 - TraineeEconomist.DELTA_PRICE) / TraineeEconomist.PERCENTAGE, this.counter);
	}
	
	public void onEquip() {
		this.counter = 0;
	}
	
	public void onMonsterDeath(AbstractMonster m) {
		this.counter += m.type.ordinal() + 1;
		this.updateDescription();
	}
	
	public double gainGold(double amount) {
		this.flash();
		return amount * goldRate();
	}

	public void update() {
		super.update();
		if (this.isObtained && this.isActive && AbstractDungeon.currMapNode != null && !used) {
			if (AbstractDungeon.getCurrRoom() instanceof ShopRoom) {
				if (!TraineeEconomist.checkDFshop(AbstractDungeon.getCurrRoom())) {
					TraineeEconomist.addDiscount(p().relics.stream().filter(r -> r instanceof Economist)
							.peek(r -> r.beginLongPulse()).map(r -> ((Economist) r).rate()).reduce(1f, (a, b) -> a * b));
					used = true;
				}
			}
		}
	}
	
	public void onEnterRoom(AbstractRoom r) {
		used = false;
		if (r instanceof ShopRoom) {
			if (!TraineeEconomist.checkDFshop(r)) {
				this.flash();
				this.beginLongPulse();
			}
		} else {
			this.stopPulse();
		}
	}
	
	private Stream<AbstractMonster> nonBoss() {
		return AbstractDungeon.getMonsters().monsters.stream()
				.filter(m -> m.type != EnemyType.BOSS && !m.isDeadOrEscaped());
	}

	@Override
	public void onRightClick() {
		if (this.inCombat() && this.hasEnemies() && nonBoss().count() > 0) {
			int hp = nonBoss().mapToInt(m -> m.maxHealth).min().getAsInt();
			if (p().gold >= 10 * hp) {
				EscapePotion.escape(nonBoss().filter(a -> a.maxHealth == hp).findAny().get(), false);
				p().loseGold(10 * hp);
			}
		} else if (!this.inCombat() && p().gold > 999) {
			p().loseGold(p().gold);
			this.counter++;
			this.updateDescription();
			this.addRandomKey();
			this.show();
		}
	}

}