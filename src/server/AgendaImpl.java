package server;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.omg.CosNaming.Binding;
import org.omg.CosNaming.BindingIteratorHolder;
import org.omg.CosNaming.BindingListHolder;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContext;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.CosNaming.NamingContextHelper;

import agenda.Agenda;
import agenda.AgendaHelper;
import agenda.AgendaPOA;
import agenda.Contact;
import agenda.ContactAlreadyExists;
import constants.Action;

public class AgendaImpl extends AgendaPOA {

	private NamingContext namingContext;
	private NamingContextExt namingContextExt;

	private String name;
	private Map<String, Contact> contacts;
	private Map<String, Agenda> nearbyAgendas;

	public AgendaImpl(String name, org.omg.CORBA.Object nameService) {

		this.namingContext = NamingContextHelper.narrow(nameService);
		this.namingContextExt = NamingContextExtHelper.narrow(nameService);

		this.name = name;
		this.contacts = new HashMap<String, Contact>();
		this.nearbyAgendas = new ConcurrentHashMap<String, Agenda>();

		this.loadAllAgendas();

	}

	@Override
	public void addContact(Contact contact) throws ContactAlreadyExists {
		boolean isValid = validadeContact(contact, this.contacts);

		if (!isValid) {
			throw new ContactAlreadyExists(contact.name);
		}

		this.contacts.put(contact.name, contact);
	}

	@Override
	public void syncContact(Contact c, String[] agendas, String action) {

		System.out.println("\n\nCurrent agenda: " + this.name);
		System.out.println("Already visited: " + agendas.length + " agendas");
		System.out.println("To visit: " + this.nearbyAgendas.size());

		if (agendas.length == nearbyAgendas.size() + 1) {
			return;
		}

		List<String> list = new ArrayList<String>(Arrays.asList(agendas));

		for (Entry<String, Agenda> entry : this.nearbyAgendas.entrySet()) {
			String name = entry.getKey();
			Agenda agenda = entry.getValue();

			System.out.println("Doing stuff to " + name);

			try {

				if (!list.contains(name)) {

					if (action.equals(Action.INSERT)) {
						try {
							agenda.addContact(c);
						} catch (ContactAlreadyExists e) {

						}

					} else if (action.equals(Action.DELETE)) {
						agenda.deleteContact(c);
					} else if (action.equals(Action.UPDATE)) {
						agenda.updateContact(c);
					}

					list.add(name);

				} else {
					continue;
				}
			} catch (Exception e) {

				if (e.getCause() instanceof java.net.ConnectException) {
					NameComponent[] nameComponents = { new NameComponent(name, "Agenda") };

					try {
						this.namingContext.unbind(nameComponents);
					} catch (Exception e1) {

					}

					this.nearbyAgendas.remove(name);
					continue;
				}
			}

			agenda.syncContact(c, list.toArray(new String[list.size()]), action);

		}
	}

	@Override
	public Contact[] getContacts() {

		Contact[] contactsArray = new Contact[this.contacts.size()];

		int i = 0;

		for (Object obj : this.contacts.values()) {
			contactsArray[i] = (Contact) obj;
			i++;
		}

		return contactsArray;

	}

	@Override
	public void updateContact(Contact contact) {
		if (!"".equals(contact.name) && !"".equals(contact.phoneNumber)) {
			Contact c = this.contacts.get(contact.name);
			c.phoneNumber = contact.phoneNumber;
		}
	}

	@Override
	public void deleteContact(Contact contact) {
		this.contacts.remove(contact.name);
	}

	private final boolean validadeContact(Contact contact, Map<String, Contact> contacts) {
		boolean isValid = true;

		if (contacts.get(contact.name) != null) {
			isValid = false;
		}

		return isValid;
	}

	private Object[] loadAllAgendaNames() {
		BindingListHolder bindingListHolder = new BindingListHolder();
		BindingIteratorHolder bindingIteratorHolder = new BindingIteratorHolder();
		this.namingContextExt.list(1000, bindingListHolder, bindingIteratorHolder);
		Binding bindings[] = bindingListHolder.value;
		List<String> agendas = new ArrayList<String>();

		for (Binding b : bindings) {
			String name = b.binding_name[0].id;
			agendas.add(name);
		}

		return agendas.toArray();
	}

	private void loadAllAgendas() {
		Object[] names = this.loadAllAgendaNames();

		for (Object string : names) {

			String name = (String) string;

			try {
				Agenda a = this.loadAgenda(name);
				this.nearbyAgendas.put(name, a);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	private Agenda loadAgenda(String s) throws Exception {
		NameComponent[] name = { new NameComponent(s, "Agenda") };
		org.omg.CORBA.Object objRef = namingContext.resolve(name);
		Agenda agenda = AgendaHelper.narrow(objRef);
		return agenda;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public void addNearbyAgenda(String name) {
		try {
			Agenda agenda = this.loadAgenda(name);
			this.nearbyAgendas.put(name, agenda);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void start() {

		if (this.nearbyAgendas.size() > 0) {
			Agenda a = new ArrayList<Agenda>(nearbyAgendas.values()).get(0);

			for (Contact c : a.getContacts()) {
				this.contacts.put(c.name, c);
			}
		}

		for (Agenda agenda : nearbyAgendas.values()) {
			agenda.addNearbyAgenda(this.getName());
		}

	}
}
