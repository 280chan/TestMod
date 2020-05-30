package relics;

import java.util.function.Predicate;

import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.cards.AbstractCard;

public abstract class AbstractBottleRelic extends MyRelic implements Predicate<AbstractCard> {
	protected boolean cardSelected = true;
	public AbstractCard card = null;
	
	public AbstractBottleRelic(String id, Texture texture, RelicTier tier, LandingSound sfx) {
		super(id, texture, tier, sfx);
	}
	
}