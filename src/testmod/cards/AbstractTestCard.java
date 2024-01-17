package testmod.cards;

import java.util.HashMap;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import basemod.abstracts.CustomCard;
import testmod.mymod.TestMod;
import testmod.utils.MiscMethods;

public abstract class AbstractTestCard extends CustomCard implements MiscMethods {
	
	/**
	 * 无色牌用
	 * @param shortID
	 * @param NAME
	 * @param IMG
	 * @param COST
	 * @param DESCRIPTION
	 * @param type
	 * @param rarity
	 * @param target
	 */
	public AbstractTestCard(String shortID, String NAME, int COST, String DESCRIPTION, CardType type,
			CardRarity rarity, CardTarget target) {
		super(TestMod.makeID(shortID), NAME, TestMod.cardIMGPath(shortID), COST, DESCRIPTION, type,
				CardColor.COLORLESS, rarity, target);
	}

	/**
	 * 诅咒用
	 * @param shortID
	 * @param NAME
	 * @param IMG
	 * @param COST
	 * @param DESCRIPTION
	 */
	public AbstractTestCard(String shortID, String NAME, String DESCRIPTION) {
		super(TestMod.makeID(shortID), NAME, TestMod.cardIMGPath(shortID), 1, DESCRIPTION, CardType.CURSE,
				CardColor.CURSE, CardRarity.SPECIAL, CardTarget.NONE);
	}
	
	private AbstractTestCard(String shortID, int COST, CardType type, CardRarity rarity, CardTarget target) {
		this(shortID, Strings(shortID).NAME, COST, Strings(shortID).DESCRIPTION, type, rarity, target);
	}
	
	public AbstractTestCard(int cost, CardType type, CardRarity rarity, CardTarget target) {
		this(shortID(getCardClass()), cost, type, rarity, target);
	}
	
	private AbstractTestCard(String shortID, CardStats stats) {
		this(shortID, stats.cost, stats.type, stats.rarity, stats.target);
		this.baseBlock = stats.baseBlock;
		this.baseDamage = stats.baseDamage;
		this.magicNumber = this.baseMagicNumber = stats.baseMagic;
		this.exhaust = stats.exhaust;
		this.isInnate = stats.innate;
		this.isEthereal = stats.ethereal;
	}
	
	public AbstractTestCard(String shortID) {
		this(shortID, stats(shortID));
	}
	
	public AbstractTestCard() {
		this(shortID(getCardClass()));
	}

	protected static CardStrings Strings(String ID) {
		if (!CS.containsKey(ID))
			CS.put(ID, CardCrawlGame.languagePack.getCardStrings(TestMod.makeID(ID)));
		return CS.get(ID);
	}
	
	protected static String name(String id) {
		return Strings(id).NAME;
	}
	
	protected static String desc(String id) {
		String tmp = Strings(id).DESCRIPTION;
		return tmp == null ? "" : tmp;
	}
	
	protected static String desc1(String id) {
		return Strings(id).UPGRADE_DESCRIPTION;
	}
	
	protected static String[] extendDesc(String id) {
		return Strings(id).EXTENDED_DESCRIPTION;
	}
	
	protected String[] exDesc() {
		return extendDesc(shortID());
	}
	
	protected String desc() {
		return desc(shortID());
	}
	
	protected String upgradedDesc() {
		return desc1(shortID());
	}
	
	public void upDesc(String desc) {
		this.rawDescription = desc;
		this.initializeDescription();
	}
	
	public void upDesc() {
		this.rawDescription = desc1(shortID());
		this.initializeDescription();
	}
	
	protected String name() {
		return name(shortID());
	}
	
	protected String shortID() {
		return shortID(this.getClass());
	}
	
	protected static <T extends AbstractTestCard> String shortID(Class<T> c) {
		IDS.putIfAbsent(c, MiscMethods.getIDWithoutLog(c));
		return IDS.get(c);
	}

	@SuppressWarnings("unchecked")
	private static <T extends AbstractTestCard> Class<T> getCardClass() {
		return MISC.get(AbstractTestCard.class);
	}
	
	protected static <T extends AbstractTestCard> CardStats stats(String id) {
		if (STAT.containsKey(id))
			return STAT.get(id);
		STAT.put(id, new CardStats(MISC.uiString(id + "Stat").TEXT));
		return STAT.get(id);
	}

	private static final HashMap<String, CardStrings> CS = new HashMap<String, CardStrings>();
	private static final HashMap<Class<? extends AbstractTestCard>, String> IDS = 
			new HashMap<Class<? extends AbstractTestCard>, String>();
	private static final HashMap<String, CardStats> STAT = new HashMap<String, CardStats>();
	
	public static class CardStats {
		private int cost, baseBlock, baseDamage, baseMagic;
		private boolean exhaust, innate, ethereal;
		private CardType type;
		private CardRarity rarity;
		private CardTarget target;
		
		public CardStats(int cost, CardType type, CardRarity rarity, CardTarget target, int block, int damage,
				int magic, boolean exhaust, boolean innate, boolean ethereal) {
			this.cost = cost;
			this.type = type;
			this.rarity = rarity;
			this.target = target;
			this.baseBlock = block;
			this.baseDamage = damage;
			this.baseMagic = magic;
			this.exhaust = exhaust;
			this.innate = innate;
			this.ethereal = ethereal;
		}
		
		public CardStats(String[] data) {
			this(Integer.parseInt(data[0]), CardType.valueOf(data[1]), CardRarity.valueOf(data[2]),
					CardTarget.valueOf(data[3]), Integer.parseInt(data[4]), Integer.parseInt(data[5]),
					Integer.parseInt(data[6]), Boolean.valueOf(data[7]), Boolean.valueOf(data[8]),
					Boolean.valueOf(data[9]));
		}
	}
	
}
