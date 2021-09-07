package relics;

import java.util.ArrayList;
import java.util.stream.Collectors;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import utils.MiscMethods;

public class AssaultLearning extends AbstractTestRelic implements MiscMethods {
	public static final String ID = "AssaultLearning";

	public AssaultLearning() {
		super(ID, RelicTier.COMMON, LandingSound.MAGICAL);
	}
	
	public String getUpdatedDescription() {
		return DESCRIPTIONS[0];
	}
	
	public void atTurnStartPostDraw() {
		this.addTmpActionToBot(() -> {
			ArrayList<AbstractCard> list = AbstractDungeon.player.drawPile.group.stream()
					.filter(AbstractCard::canUpgrade).collect(Collectors.toCollection(ArrayList::new));
			if (!list.isEmpty()) {
				list.get(list.size() - 1).upgrade();
				this.show();
			}
		});
    }
	
}