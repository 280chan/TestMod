package testmod.relicsup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.stream.Stream;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import testmod.mymod.TestMod;
import testmod.relics.AbstractTestRelic;
import testmod.relics.Charity;
import testmod.relics.Faith;
import testmod.relics.Fortitude;
import testmod.relics.Hope;
import testmod.relics.Justice;
import testmod.relics.Prudence;
import testmod.relics.Sins;
import testmod.relics.Temperance;

public class SinsUp extends AbstractUpgradedRelic {
	public static final AbstractCard[] SINS = Sins.SINS;
	public static final AbstractTestRelic[] RELICS = { new Charity(), new Faith(), new Fortitude(), new Hope(),
			new Justice(), new Prudence(), new Temperance() };
	
	public void onEquip() {
		int a = (int) Stream.of(SINS)
				.filter(s -> p().masterDeck.group.stream().anyMatch(c -> c.cardID.equals(s.cardID))).count();
		ArrayList<AbstractTestRelic> l = Stream.of(RELICS).collect(toArrayList());
		if (a == 7) {
			l.replaceAll(r -> r.upgrade());
		} else if (a > 0) {
			ArrayList<AbstractTestRelic> tmp = l.stream().collect(toArrayList());
			Collections.shuffle(tmp, new Random(AbstractDungeon.miscRng.randomLong()));
			tmp.stream().limit(a).forEach(r -> l.set(l.indexOf(r), r.upgrade()));
			tmp.clear();
		}
		this.addTmpEffect(() -> l.forEach(r -> TestMod.obtain(p(), r, true)));
    }

}