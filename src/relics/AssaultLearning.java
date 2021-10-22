package relics;

import java.util.ArrayList;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class AssaultLearning extends AbstractTestRelic {

	public AssaultLearning() {
		super(RelicTier.COMMON, LandingSound.MAGICAL);
	}
	
	public void atTurnStartPostDraw() {
		this.addTmpActionToBot(() -> {
			ArrayList<AbstractCard> list = AbstractDungeon.player.drawPile.group.stream()
					.filter(AbstractCard::canUpgrade).collect(this.collectToArrayList());
			if (!list.isEmpty()) {
				list.get(list.size() - 1).upgrade();
				this.show();
			}
		});
    }
	
}