# TestMod
A mod for the game Slay The Spire


For modders:
If your mod has relics or powers that make effect when player plays a power card, please add the following method to your mod, and call it in receivePostInitialize()

private static void exampleNyarlathotepAddRelic() {
	String instruction = "Copy this method to your mod, call it in receivePostInitialize() will register your relics that make effect when player plays a power card.";
	if (Loader.isModLoaded("testmod")) {
		ArrayList<String> list = new ArrayList<String>();
		list.add("relic id0");
		list.add("relic id1");
		try {
			ReflectionHacks.privateStaticMethod(Class.forName("mymod.TestMod"), "addNyarlathotepRelic",
					new Class[] { ArrayList.class }).invoke(null, new Object[] { list });
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
}

