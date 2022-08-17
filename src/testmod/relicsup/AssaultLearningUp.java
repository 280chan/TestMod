package testmod.relicsup;

import java.util.ArrayList;
import java.util.stream.Stream;

import com.megacrit.cardcrawl.cards.AbstractCard;

public class AssaultLearningUp extends AbstractUpgradedRelic {
	
	private void addCardFromList(ArrayList<AbstractCard> list, ArrayList<AbstractCard> source) {
		AbstractCard t = source.stream().filter(c -> c.canUpgrade()).findFirst().orElse(null);
		if (t != null) {
			list.add(t);
			t = source.stream().filter(c -> c.canUpgrade()).reduce(this::last).orElse(null);
			if (t != null)
				list.add(t);
		}
	}
	
	private void upgradeIfPossible(AbstractCard c) {
		if (c.canUpgrade())
			c.upgrade();
	}
	
	public void atTurnStartPostDraw() {
		this.addTmpActionToBot(() -> {
			ArrayList<AbstractCard> list = new ArrayList<AbstractCard>();
			Stream.of(p().drawPile, p().hand, p().discardPile).map(g -> g.group).forEach(l -> addCardFromList(list, l));
			if (!list.isEmpty())
				this.show();
			list.forEach(this::upgradeIfPossible);
			list.clear();
		});
    }
	
}