package relics;

import java.util.function.Predicate;

import com.megacrit.cardcrawl.cards.AbstractCard;

public abstract class AbstractBottleRelic extends AbstractTestRelic implements Predicate<AbstractCard> {
	protected boolean cardSelected = true;
	public AbstractCard card = null;
	
	public AbstractBottleRelic(String id, RelicTier tier, LandingSound sfx) {
		super(id, tier, sfx);
	}
	
}