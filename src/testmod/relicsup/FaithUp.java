package testmod.relicsup;

import java.util.ArrayList;
import java.util.stream.Stream;

import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.DamageInfo.DamageType;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.ShopRoom;
import com.megacrit.cardcrawl.shop.*;
import basemod.ReflectionHacks;
import testmod.mymod.TestMod;
import testmod.relics.Faith;
import testmod.utils.CounterKeeper;

public class FaithUp extends AbstractUpgradedRelic implements CounterKeeper {
	
	public void atPreBattle() {
		if (this.isActive) {
			int tmp = this.counter * relicCount();
			if (tmp > 49)
				this.atb(this.apply(p(), new StrengthPower(p(), tmp / 50)));
		}
	}
	
	public void onEquip() {
		TestMod.setActivity(this);
		this.counter = 0;
		if (!this.isActive)
			return;
		if (AbstractDungeon.floorNum > 0 && AbstractDungeon.getCurrRoom() instanceof ShopRoom) {
			this.gainGold();
		}
	}
	
	private int relicCount() {
		return (int) (this.relicStream(FaithUp.class).count() + this.relicStream(Faith.class).count());
	}
	
	private void gainGold() {
		this.addTmpEffect(() -> {
			ShopScreen ss = AbstractDungeon.shopScreen;
			int max = Math.max(ShopScreen.actualPurgeCost, 0);
			max = Math.max(max, Stream.of(ss.coloredCards, ss.colorlessCards).flatMap(s -> s.stream())
					.mapToInt(c -> c.price).max().orElse(0));
			ArrayList<StoreRelic> relics = ReflectionHacks.getPrivate(ss, ShopScreen.class, "relics");
			max = Math.max(max, relics.stream().mapToInt(r -> r.price).max().orElse(0));
			ArrayList<StorePotion> potions = ReflectionHacks.getPrivate(ss, ShopScreen.class, "potions");
			max = Math.max(max, potions.stream().mapToInt(p -> p.price).max().orElse(0));
			this.counter += max;
			max *= relicCount();
			p().gainGold(max);
			this.show();
		});
	}
	
	private void act() {
		int tmp = Math.max(1, this.counter / 25);
		if (p().gold < tmp) {
			p().damage(new DamageInfo(null, tmp - p().gold, DamageType.THORNS));
			p().loseGold(p().gold);
		} else {
			p().loseGold(tmp);
		}
		this.counter -= tmp;
	}
	
	public void justEnteredRoom(final AbstractRoom room) {
		if (this.isActive) {
			if (room instanceof ShopRoom) {
				this.gainGold();
			} else if (this.counter > 0) {
				this.addTmpEffect(() -> {
					if (this.inCombat()) {
						this.addTmpActionToTop(() -> act());
					} else {
						act();
					}
				});
			}
		}
	}
}