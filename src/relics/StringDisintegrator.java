package relics;

import java.util.HashMap;
import java.util.stream.Stream;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.CardGroup.CardGroupType;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rooms.AbstractRoom.RoomPhase;

public class StringDisintegrator extends AbstractTestRelic {
	
	private static final String EMPTY = "";
	private static final HashMap<AbstractCard, String> DESC = new HashMap<AbstractCard, String>();
	private static final HashMap<AbstractCard, String> NAME = new HashMap<AbstractCard, String>();
	private static boolean trigger = false;
	

	public StringDisintegrator() {
		super(RelicTier.BOSS, LandingSound.HEAVY);
	}
	
	private static void hideAllText() {
		AbstractPlayer p = AbstractDungeon.player;
		Stream.of(p.discardPile, p.drawPile, p.exhaustPile, p.hand, p.limbo, p.masterDeck)
				.forEach(StringDisintegrator::hideText);
	}
	
	private static void hideText(CardGroup g) {
		if (g.type == CardGroupType.MASTER_DECK) {
			hideDeckText(g);
		} else {
			g.group.forEach(StringDisintegrator::hideText);
		}
	}
	
	private static void hideDeckText(CardGroup g) {
		g.group.forEach(StringDisintegrator::hideDeckText);
	}
	
	private static void hideDeckText(AbstractCard c) {
		recordDeck(c);
		hideText(c);
	}
	
	private static void hideText(AbstractCard c) {
		if (!c.rawDescription.equals(EMPTY)) {
			c.rawDescription = EMPTY;
			c.initializeDescription();
		}
		if (!c.name.equals(EMPTY))
			c.name = EMPTY;
	}
	
	private static void recordDeck(AbstractCard c) {
		if (!DESC.containsKey(c))
			DESC.put(c, c.rawDescription);
		if (!NAME.containsKey(c))
			NAME.put(c, c.name);
	}
	
	private static void loadAllText() {
		AbstractDungeon.player.masterDeck.group.forEach(StringDisintegrator::loadDeck);
	}
	
	private static void loadDeck(AbstractCard c) {
		if (DESC.containsKey(c)) {
			c.rawDescription = DESC.get(c);
			c.initializeDescription();
			DESC.remove(c);
		}
		if (NAME.containsKey(c)) {
			c.name = NAME.get(c);
			NAME.remove(c);
		}
	}
	
	public void update() {
		super.update();
		if (!this.isActive)
			return;
		if (AbstractDungeon.currMapNode != null && AbstractDungeon.getCurrRoom().phase == RoomPhase.COMBAT) {
			if (trigger)
				hideAllText();
		}
	}
	
	public void atPreBattle() {
		if (!this.isActive)
			return;
		trigger = true;
	}
	
	public void onEquip() {
		this.addEnergy();
    }
	
	public void onUnequip() {
		this.reduceEnergy();
    }
	
	public void onVictory() {
		if (!this.isActive)
			return;
		trigger = false;
		loadAllText();
    }
	
}