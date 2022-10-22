package testmod.relicsup;

import java.util.stream.Stream;
import com.megacrit.cardcrawl.actions.common.ExhaustSpecificCardAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard.CardType;
import halloweenMod.cards.Halloween;
import halloweenMod.mymod.HalloweenMod;
import testmod.mymod.TestMod;

public class EventCelebration_HalloweenUp extends AbstractUpgradedRelic {
	public static final String ID = TestMod.makeID("HalloweenUp");
	public static final String IMG = "halloweenResources/images/relic.png";
	
	public static boolean hasThis() {
		return MISC.relicStream().anyMatch(r -> r instanceof EventCelebration_HalloweenUp);
	}
	
	public static Stream<EventCelebration_HalloweenUp> getThis() {
		return MISC.relicStream(EventCelebration_HalloweenUp.class);
	}
	
	public EventCelebration_HalloweenUp() {
		super(ID, IMG, RelicTier.SPECIAL, LandingSound.MAGICAL);
	}
	
	public void onCardDraw(AbstractCard c) {
		if (c.type == CardType.CURSE || c.type == CardType.STATUS) {
			this.att(new ExhaustSpecificCardAction(c, p().hand, true));
			this.atb(new MakeTempCardInHandAction(new Halloween()));
			this.flash();
		} else if (c instanceof Halloween) {
			c.upgrade();
			c.superFlash();
			this.flash();
		}
	}
	
	public void onEquip() {
		HalloweenMod.savedFloorNum = -2;
		HalloweenMod.changeState();
	}
	
}