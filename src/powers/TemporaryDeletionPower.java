package powers;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Map;

import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard.CardColor;
import com.megacrit.cardcrawl.cards.AbstractCard.CardRarity;
import com.megacrit.cardcrawl.cards.AbstractCard.CardType;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.CardGroup.CardGroupType;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.powers.AbstractPower;

import basemod.BaseMod;
import mymod.TestMod;

public class TemporaryDeletionPower extends AbstractPower {
	public static final String POWER_ID = "TemporaryDeletionPower";
	public static final String NAME = "临时删除";
    public static final String IMG = TestMod.powerIMGPath(POWER_ID);
	public static final String[] DESCRIPTIONS = {"每回合开始将", "张随机", "牌加入手牌。"};

	private static final String[] RARITY = { "基础", "特殊", "普通", "罕见", "稀有", "诅咒" };
	private CardRarity rarity;
	private CardGroup group;
	private int index = 0;
	
	private boolean fuckedRarity;
	
	public TemporaryDeletionPower(AbstractCreature owner, int amount, AbstractCard c) {
		this.owner = owner;
		this.amount = amount;

		this.fuckedRarity = c.getClass().getSuperclass().getCanonicalName().equals("lobotomyMod.card.AbstractLobotomyCard");
		
		ArrayList<AbstractCard> list = getList(c.rarity, c.color);
		
		// ???
		if (c.rarity != CardRarity.BASIC && c.rarity != CardRarity.SPECIAL && c.color == CardColor.COLORLESS && c.type != CardType.STATUS) {
			list.addAll(getList(c.rarity, c.color));
		}
		
		this.group = getGroup(list);
		this.img = ImageMaster.loadImage(IMG);
		this.index = c.rarity.ordinal();
		this.rarity = c.rarity;
		this.name = NAME + this.rarityDesc();
		this.ID = POWER_ID + c.color + this.index;
		
		
		updateDescription();
		this.type = PowerType.BUFF;
	}
	
	private String rarityDesc() {
		if (this.index < RARITY.length) {
			return "[" + RARITY[index] + "]";
		} else {
			return "[" + this.rarity.toString() + "]";
		}
	}
	
	public void updateDescription() {
		 this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1] + this.rarityDesc() + DESCRIPTIONS[2];
	}
	
	public void stackPower(final int stackAmount) {
		this.fontScale = 8.0f;
        this.amount += stackAmount;
	}
	
	private ArrayList<AbstractCard> getList(CardRarity rarity, CardColor color) {
		ArrayList<AbstractCard> list = new ArrayList<AbstractCard>();
		for (Map.Entry<String, AbstractCard> c : CardLibrary.cards.entrySet()) {
			AbstractCard card = (AbstractCard) c.getValue();
			if ((card.color == color) && (card.rarity == rarity)) {
				list.add(card);
			}
		}
		if (list.isEmpty()) {
			for (AbstractCard c : BaseMod.getCustomCardsToAdd()) {
				if (c.color == color && fuckedRarity) {
					AbstractCard tmp = c.makeCopy();
					try {
						c = tmp;
						c.getClass().getMethod("initInfo").invoke(tmp);
						if (c.getClass().getField("realRarity").get(c) == rarity) {
							list.add(c);
							continue;
						}
					} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
							| NoSuchMethodException | SecurityException | NoSuchFieldException e) {
						e.printStackTrace();
					}
				} else if (c.color == color && c.rarity == rarity) {
					list.add(c);
				}
			}
		}
		System.out.println("数组大小 = " + list.size());
		return list;
	}
	
	private static CardGroup getGroup(ArrayList<AbstractCard> list) {
		CardGroup group = new CardGroup(CardGroupType.UNSPECIFIED);
		group.group = list;
		return group;
	}
	
	public void atStartOfTurn() {
		if (!AbstractDungeon.getMonsters().areMonstersBasicallyDead()) {
			flash();
			for (int i = 0; i < this.amount; i++) {
				this.addToBot(new MakeTempCardInHandAction(group.getRandomCard(AbstractDungeon.cardRandomRng).makeCopy(), 1, false));
			}
		}
	}
    
}
