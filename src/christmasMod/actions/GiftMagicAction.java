package christmasMod.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ObtainPotionAction;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class GiftMagicAction extends AbstractGameAction {
	private static final float DURATION = Settings.ACTION_DUR_XFAST;
	
	public GiftMagicAction() {
		this.actionType = ActionType.SPECIAL;
		this.duration = DURATION;
	}

	@Override
	public void update() {
		this.isDone = true;
		for (int i = 0; i < AbstractDungeon.player.potionSlots; i++) {
			addToTop(new ObtainPotionAction(AbstractDungeon.returnRandomPotion(true)));
		}
	}

}
