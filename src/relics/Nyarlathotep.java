package relics;

import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard.CardType;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;

import mymod.TestMod;

public class Nyarlathotep extends AbstractTestRelic {
	public static final String ID = "Nyarlathotep";

	private static final String[] POWER_IDs = { "Amplify", "Heatsink", "Storm", "Curiosity",
			TestMod.makeID("PlaguePower") };
	private static final String[] RELIC_IDs = { "Bird Faced Urn", "Mummified Hand", "OrangePellets",
			"paleoftheancients:SoulOfTheDefect", "Replay:Rubber Ducky", "Dota2Spire:ArcaneBoots",
			"Dota2Spire:EtherealBlade", "Dota2Spire:OrchidMalevolence", "Dota2Spire:AghanimScepter",
			"DemoExt:GalacticMedalOfValor", "SynthV:C4", "Clover", "SynthV:MejaisSoulstealer",
			"youkari:Boundary_crack" };

	public Nyarlathotep() {
		super(ID, RelicTier.RARE, LandingSound.MAGICAL);
	}
	
	private static boolean isThis(AbstractRelic r) {
		return r instanceof Nyarlathotep;
	}
	
	public String getUpdatedDescription() {
		return DESCRIPTIONS[0];
	}
	
	public void onUseCard(final AbstractCard c, final UseCardAction action) {
		this.triggerExhaustFor(c);
		this.triggerDiscardFor(c);
		this.triggerUsePowerCard(c, action);
		this.show();
	}
	
	private void triggerExhaustFor(AbstractCard c) {
		for (AbstractRelic r : AbstractDungeon.player.relics)
			if (!isThis(r))
				r.onExhaust(c);
		for (AbstractPower p : AbstractDungeon.player.powers)
			p.onExhaust(c);
		c.triggerOnExhaust();
	}
	
	private void triggerDiscardFor(AbstractCard c) {
        c.triggerOnManualDiscard();
        ++GameActionManager.totalDiscardedThisTurn;
		if (AbstractDungeon.actionManager.turnHasEnded)
			return;
		AbstractDungeon.player.updateCardsOnDiscard();
		for (AbstractRelic r : AbstractDungeon.player.relics)
			if (!isThis(r))
				r.onManualDiscard();
	}
	
	private void triggerUsePowerCard(AbstractCard c, UseCardAction a) {
		AbstractPlayer p = AbstractDungeon.player;
		AbstractCard tmp = c.makeSameInstanceOf();
		tmp.type = CardType.POWER;
		
		for (String id : POWER_IDs)
			if (p.hasPower(id))
				p.getPower(id).onUseCard(tmp, a);
		for (String id : RELIC_IDs)
			if (p.hasRelic(id))
				p.getRelic(id).onUseCard(tmp, a);
	}
	
	public void onExhaust(final AbstractCard c) {
		this.triggerDiscardFor(c);
		this.show();
    }
	
	public void onManualDiscard() {
		this.triggerExhaustFor(AbstractDungeon.player.discardPile.getTopCard());
		this.show();
    }
	
}