package relics;

import java.util.HashMap;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.CardGroup.CardGroupType;
import com.megacrit.cardcrawl.characters.AbstractPlayer.PlayerClass;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.rooms.AbstractRoom.RoomPhase;

public class StringDisintegrator extends AbstractTestRelic {
	public static final String ID = "StringDisintegrator";
	
	private static final String EMPTY = "";
	private static final HashMap<AbstractCard, String> DESC = new HashMap<AbstractCard, String>();
	private static final HashMap<AbstractCard, String> NAME = new HashMap<AbstractCard, String>();
	private static boolean trigger = false;
	

	public StringDisintegrator() {
		super(ID, RelicTier.BOSS, LandingSound.HEAVY);
	}
	
	public String getUpdatedDescription() {
		return this.DESCRIPTIONS[0];
	}

	public void updateDescription(PlayerClass c) {
		this.tips.clear();
	    this.tips.add(new PowerTip(this.name, this.getUpdatedDescription()));
	    initializeTips();
	}
	
	private static void hideAllText() {
		hideText(AbstractDungeon.player.discardPile);
		hideText(AbstractDungeon.player.drawPile);
		hideText(AbstractDungeon.player.exhaustPile);
		hideText(AbstractDungeon.player.hand);
		hideText(AbstractDungeon.player.limbo);
		hideText(AbstractDungeon.player.masterDeck);
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
		AbstractDungeon.player.energy.energyMaster++;
    }
	
	public void onUnequip() {
		AbstractDungeon.player.energy.energyMaster--;
    }
	
	public void onVictory() {
		if (!this.isActive)
			return;
		trigger = false;
		loadAllText();
    }
	
}