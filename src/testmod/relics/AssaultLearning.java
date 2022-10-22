package testmod.relics;

import com.megacrit.cardcrawl.cards.AbstractCard;

public class AssaultLearning extends AbstractTestRelic {
	
	public void atTurnStartPostDraw() {
		this.addTmpActionToBot(() -> {
			AbstractCard t = p().drawPile.group.stream().filter(AbstractCard::canUpgrade).reduce(this::last)
					.orElse(null);
			if (t != null) {
				t.upgrade();
				this.show();
			}
		});
	}
	
}