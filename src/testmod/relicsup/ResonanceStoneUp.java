package testmod.relicsup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Random;
import java.util.stream.Collectors;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import basemod.Pair;
import testmod.utils.CounterKeeper;
import testmod.utils.ResonanceTrigger;

public class ResonanceStoneUp extends AbstractUpgradedRelic implements ResonanceTrigger, CounterKeeper {
	public static HashMap<AbstractCard, Integer> previous = new HashMap<AbstractCard, Integer>();
	boolean init = true;
	
	public ResonanceStoneUp() {
		this.counter = 1;
	}
	
	public void onMasterDeckChange() {
		if (!this.isActive)
			return;
		previous.keySet().retainAll(p().masterDeck.group);
		p().masterDeck.group.forEach(c -> previous.putIfAbsent(c, c.timesUpgraded));
	}
	
	public void update() {
		super.update();
		if (!this.isActive || !this.isObtained || p() == null || p().masterDeck == null)
			return;
		if (init) {
			previous.clear();
			previous.putAll(p().masterDeck.group.stream().collect(Collectors.toMap(t(), c -> c.timesUpgraded)));
			init = false;
		} else {
			ArrayList<Pair<AbstractCard, Integer>> upgradeTimes = previous.entrySet().stream().filter(e -> delta(e) > 0)
					.map(split(e -> e.getKey(), this::delta)).collect(toArrayList());

			stream().forEach(r -> r.doSth(upgradeTimes));
			previous.entrySet().stream().filter(e -> delta(e) > 0).forEach(e -> e.setValue(e.getKey().timesUpgraded));
		}
	}
	
	public void doSth(ArrayList<Pair<AbstractCard, Integer>> upgradeTimes) {
		int tmp = this.counter;
		upgradeTimes.forEach(consumer(this::upgrade));
		if (this.counter <= tmp)
			return;
		this.getNumberList(tmp, this.counter).forEach(i -> {
			p().increaseMaxHp(i, true);
			p().gainGold(i);
		});
		this.show();
	}
	
	private void upgrade(AbstractCard c) {
		c.upgrade();
		this.counter++;
	}
	
	private void upgrade(AbstractCard a, int b) {
		this.getNaturalNumberList(b).forEach(i -> p().masterDeck.group.stream()
				.filter(c -> c.cardID.equals(a.cardID)).filter(c -> c.canUpgrade()).forEach(this::upgrade));
	}
	
	private int delta(Entry<AbstractCard, Integer> e) {
		return e.getKey().timesUpgraded - e.getValue();
	}
	
	public void upgrade(RelicTier t) {
		this.addTmpEffect(() -> {
			ArrayList<AbstractRelic> l = p().relics.stream().filter(r -> r.tier == t && canUpgrade(r))
					.collect(toArrayList());
			if (!l.isEmpty()) {
				Collections.shuffle(l, new Random(AbstractDungeon.miscRng.randomLong()));
				AbstractRelic r0 = l.get(0);
				r0.onUnequip();
				this.tryUpgrade(r0).instantObtain(p(), p().relics.indexOf(r0), true);
				l.clear();
				this.show();
			}
		});
	}

}