package testmod.utils;

import java.util.ArrayList;
import java.util.stream.Stream;

import com.megacrit.cardcrawl.cards.AbstractCard;
import basemod.Pair;

public interface ResonanceTrigger {
	void doSth(ArrayList<Pair<AbstractCard, Integer>> upgradeTimes);
	
	default Stream<ResonanceTrigger> stream() {
		return MiscMethods.MISC.p().relics.stream().filter(r -> r instanceof ResonanceTrigger)
				.map(r -> (ResonanceTrigger) r);
	}
}
