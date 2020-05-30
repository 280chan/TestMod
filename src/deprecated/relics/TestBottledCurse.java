package deprecated.relics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard.CardType;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rooms.AbstractRoom;

import relics.AbstractBottleRelic;

/**
 * @deprecated
 */
public class TestBottledCurse extends AbstractBottleRelic {
	public static final String ID = "BottledCurse";//遗物Id，添加遗物、替换遗物时填写该id而不是遗物类名。
	public static final String IMG = "resources/images/relic1.png";//遗物图片路径
	public static final String DESCRIPTION = "拾起时，选择一张 #y诅咒牌 。在每场战斗开始时，这张牌会出现在手牌中。如果成功选择，每回合开始获得 [R] 。";//遗物效果的文本描叙。
	
	public TestBottledCurse() {
		super(ID, new Texture(Gdx.files.internal(IMG)), RelicTier.BOSS, LandingSound.HEAVY);
	}
	
	public String getUpdatedDescription() {
		return DESCRIPTIONS[0];
	}

	public void onEquip() {
		this.cardSelected = false;
		if (AbstractDungeon.isScreenUp) {
			AbstractDungeon.dynamicBanner.hide();
			AbstractDungeon.overlayMenu.cancelButton.hide();
			AbstractDungeon.previousScreen = AbstractDungeon.screen;
		}
		AbstractDungeon.getCurrRoom().phase = AbstractRoom.RoomPhase.INCOMPLETE;
		AbstractDungeon.gridSelectScreen.open(AbstractDungeon.player.masterDeck.getCardsOfType(CardType.CURSE), 1, this.DESCRIPTIONS[1] + this.name + ".", false, false, false, false);
    }//触发时机：当玩家获得该遗物时。(参考灵体外质、诅咒钥匙、天鹅绒项圈等)
	
	public void onUnequip() {
		if (this.card != null) {
			AbstractCard cardInDeck = AbstractDungeon.player.masterDeck.getSpecificCard(this.card);
			if (cardInDeck != null) {
			}
		}
    }//触发时机：当玩家失去该遗物时。(参考灵体外质、诅咒钥匙、天鹅绒项圈等)
	
	public void update() {
		super.update();
		if ((!this.cardSelected) && (!AbstractDungeon.gridSelectScreen.selectedCards.isEmpty())) {
			this.cardSelected = true;
			this.card = AbstractDungeon.gridSelectScreen.selectedCards.get(0);
			AbstractDungeon.getCurrRoom().phase = AbstractRoom.RoomPhase.COMPLETE;
			AbstractDungeon.gridSelectScreen.selectedCards.clear();
			this.description = (this.DESCRIPTIONS[2] + this.card.name + this.DESCRIPTIONS[3]);
			this.tips.clear();
			this.tips.add(new PowerTip(this.name, this.description));
			initializeTips();
		}
	}
	
	@Override
	public boolean test(AbstractCard c) {
		return c.type == CardType.CURSE && c == this.card;
	}

}