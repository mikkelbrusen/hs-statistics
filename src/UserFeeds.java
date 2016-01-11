public class UserFeeds extends Thread{


	final String feed = "/space/UserData";
	final String users = "/space/Users";


	// Two tuple templates for the main loop of the app
	Template t1;
	Template t2;
	Template t3;
	
	static Semaphore multEx, druid, hunter, mage, paladin, priest,
					rogue, shaman, warlock, warrior = new Semaphore(1);
	boolean ewp = false;

	public void run(){
		try{
			while(true){

				// Template t1 represents "any file in the space folder"
				t1 = new Template(feed,"?");
				System.out.println("Looking for some file...");
				try { multEx.P(); } catch (InterruptedException e) {} // The threads should not get the same file.
				t1.get();
				multEx.V();
				System.out.println("Found file " + t1.name);

				String[] parts = t1.name.split("_");
				String[] endPart = parts[3].split("\\.");
				String winCon = endPart[0];


				t2 = new Template(users + "/" + parts[0] + "/Statistics",parts[1]);
				System.out.println("Looking for some file...");

				psem(parts[1]);
				ewp = t2.existsWithPrefix();
				
				if(ewp){

					String[] parts2 = t2.name.split("_");

					int winCount = Integer.parseInt(parts2[1]);
					int lossCount = Integer.parseInt(parts2[2]);

					if(winCon.equals("Win")){
						winCount++;
					} else {
						lossCount++;
					}

					t3 = new Template("/space/Users/" + parts[0] + "/Statistics/" + parts[1] + "_" + winCount + "_" + lossCount,t2.name);
					System.out.println("Putting the file in its corresponding subfolder...");
					t3.put();
					t2.remove();

				} else {

					if(winCon.equals("Win")){
						t3 = new Template("/space/Users/" + parts[0] + "/Statistics/" + parts[1] + "_" + 1 + "_" + 0,t2.name);
						System.out.println("Putting the file in its corresponding subfolder...");
						t3.put();
					} else {
						t3 = new Template("/space/Users/" + parts[0] + "/Statistics/" + parts[1] + "_" + 0 + "_" + 1,t2.name);
						System.out.println("Putting the file in its corresponding subfolder...");
						t3.put();
					}


				}
				vsem(parts[1]);
			}

		}
		catch (Exception e) { e.printStackTrace();
		}

	}

	private void psem(String c) {
		switch (c) {
		case "Druid": try { druid.P(); } catch (InterruptedException e) {}
		case "Hunter": try { hunter.P(); } catch (InterruptedException e) {}
		case "Mage": try { mage.P(); } catch (InterruptedException e) {}
		case "Paladin": try { paladin.P(); } catch (InterruptedException e) {}
		case "Priest": try { priest.P(); } catch (InterruptedException e) {}
		case "Rogue": try { rogue.P(); } catch (InterruptedException e) {}
		case "Shaman": try { shaman.P(); } catch (InterruptedException e) {}
		case "Warlock": try { warlock.P(); } catch (InterruptedException e) {}
		case "Warrior": try { warrior.P(); } catch (InterruptedException e) {}
		default: return;
		}
	}	

	private void vsem(String c) {
		switch (c) {
		case "Druid": druid.V();
		case "Hunter": hunter.V();
		case "Mage": mage.V();
		case "Paladin": paladin.V();
		case "Priest": priest.V();
		case "Rogue": rogue.V();
		case "Shaman": shaman.V();
		case "Warlock": warlock.V();
		case "Warrior": warrior.V();
		default: return;
		}
	}	

}
