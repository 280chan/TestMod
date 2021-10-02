package relics;

import java.util.ArrayList;
import java.util.stream.Stream;

import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard.CardType;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;

import mymod.TestMod;

public class Nyarlathotep extends AbstractTestRelic {
	public static final String ID = "Nyarlathotep";

	private static final String[] POWER_IDs = { "Amplify", "Heatsink", "Storm", "Curiosity",
			TestMod.makeID("PlagueActPower") };
	private static final String[] RELIC_IDs = { "Bird Faced Urn", "Mummified Hand", "OrangePellets",
			"paleoftheancients:SoulOfTheDefect", "Replay:Rubber Ducky", "Dota2Spire:ArcaneBoots",
			"Dota2Spire:EtherealBlade", "Dota2Spire:OrchidMalevolence", "Dota2Spire:AghanimScepter",
			"DemoExt:GalacticMedalOfValor", "SynthV:C4", "Clover", "SynthV:MejaisSoulstealer",
			"youkari:Boundary_crack", "BirthdayGift-Icosahedron" };
	private static final ArrayList<String> POWER_LIST = new ArrayList<String>();
	private static final ArrayList<String> RELIC_LIST = new ArrayList<String>();
	
	private static void addToList(ArrayList<String> target, String id) {
		if (!target.contains(id))
			target.add(id);
	}
	
	public static void addPowerList(ArrayList<String> list) {
		POWER_LIST.addAll(list);
	}
	
	public static void addPowerList(String... list) {
		for (String id : list)
			addToList(POWER_LIST, id);
	}
	
	public static void addRelicList(ArrayList<String> list) {
		RELIC_LIST.addAll(list);
	}
	
	public static void addRelicList(String... list) {
		for (String id : list)
			addToList(RELIC_LIST, id);
	}
	
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
		AbstractDungeon.player.relics.stream().filter(not(Nyarlathotep::isThis)).forEach(r -> r.onExhaust(c));
		AbstractDungeon.player.powers.forEach(p -> p.onExhaust(c));
		c.triggerOnExhaust();
	}
	
	private void triggerDiscardFor(AbstractCard c) {
        c.triggerOnManualDiscard();
        ++GameActionManager.totalDiscardedThisTurn;
		if (AbstractDungeon.actionManager.turnHasEnded)
			return;
		AbstractDungeon.player.updateCardsOnDiscard();
		AbstractDungeon.player.relics.stream().filter(not(Nyarlathotep::isThis)).forEach(r -> r.onManualDiscard());
	}
	
	private void triggerUsePowerCard(AbstractCard c, UseCardAction a) {
		AbstractPlayer p = AbstractDungeon.player;
		AbstractCard tmp = c.makeSameInstanceOf();
		tmp.type = CardType.POWER;
		Stream.concat(Stream.of(POWER_IDs), POWER_LIST.stream()).distinct().filter(p::hasPower)
				.forEach(id -> p.getPower(id).onUseCard(tmp, a));
		Stream.concat(Stream.of(RELIC_IDs), RELIC_LIST.stream()).distinct().filter(p::hasRelic)
				.forEach(id -> p.getRelic(id).onUseCard(tmp, a));
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