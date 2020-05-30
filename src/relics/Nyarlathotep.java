package relics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard.CardType;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;

import mymod.TestMod;

public class Nyarlathotep extends MyRelic {
	public static final String ID = "Nyarlathotep";
	public static final String IMG = TestMod.relicIMGPath(ID);
	
	public static final String DESCRIPTION = "你成功打出且生效的牌，同时会被额外视为被 #y消耗 和被从手牌中 #y丢弃 和使用了 #y能力牌 。你 #y消耗 或 #y丢弃 的牌，同时会被额外视为另一种效果。";//遗物效果的文本描叙。
	
	private static final String[] POWER_IDs = { "Amplify", "Heatsink", "Storm" };
	private static final String[] RELIC_IDs = { "Bird Faced Urn", "Mummified Hand", "OrangePellets",
			"paleoftheancients:SoulOfTheDefect" };

	public Nyarlathotep() {
		super(ID, new Texture(Gdx.files.internal(IMG)), RelicTier.RARE, LandingSound.MAGICAL);
	}
	
	public String getUpdatedDescription() {
		return DESCRIPTIONS[0];
	}
	
	public void onUseCard(final AbstractCard c, final UseCardAction action) {
		if (!this.isActive)
			return;
		this.triggerExhaustFor(c);
		this.triggerDiscardFor(c);
		this.triggerUsePowerCard(c, action);
		this.show();
	}
	
	private void triggerExhaustFor(AbstractCard c) {
		for (AbstractRelic r : AbstractDungeon.player.relics)
			if (!r.relicId.equals(ID))
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
			if (!r.relicId.equals(ID))
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
		if (!this.isActive)
			return;
		this.triggerDiscardFor(c);
		this.show();
    }
	
	public void onManualDiscard() {
		if (!this.isActive)
			return;
		this.triggerExhaustFor(AbstractDungeon.player.discardPile.getTopCard());
		this.show();
    }
	
}