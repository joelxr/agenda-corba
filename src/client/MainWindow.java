package client;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import org.omg.CORBA.ORB;
import org.omg.CORBA.ORBPackage.InvalidName;
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
import agenda.Contact;
import agenda.ContactAlreadyExists;

public class MainWindow {

	private final JFrame frame;
	private JTextField nameTextField;
	private JTextField numberTextField;
	private JButton addButton;
	private JTable contactsTable;
	private DefaultTableModel model;
	private TableRowSorter<TableModel> sorter;
	private JScrollPane contactsScrollPane;
	private JTextField searchTextField;
	private JLabel agendaLabel;
	private JLabel rowCountLabel;
	private JLabel statusLabel;
	private JPanel statusPanel;
	private JPanel inputPanel;
	private JPanel contactsPanel;

	private Agenda agenda;
	private String agendaName;

	private ORB orb;
	private org.omg.CORBA.Object nameService;
	private NamingContext namingContext;
	private NamingContextExt namingContextExt;

	public MainWindow(String[] args) {
		this.frame = new JFrame();

		this.initializeComponents();

		try {
			this.orb = ORB.init(args, null);
			this.nameService = orb.resolve_initial_references("NameService");
			this.namingContext = NamingContextHelper.narrow(this.nameService);
			this.namingContextExt = NamingContextExtHelper.narrow(this.nameService);
		} catch (InvalidName e) {
			e.printStackTrace();
		}
	}

	/**
	 * Method to initialize all window components
	 */
	protected void initializeComponents() {
		this.nameTextField = new TextField("Nome", 15);
		this.numberTextField = new TextField("Telefone", 15);
		this.addButton = new JButton("Adicionar");
		this.addButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				addButton_Click();
			}
		});
		this.agendaLabel = new JLabel();
		this.statusLabel = new JLabel("Última sincronização: nunca.");
		this.statusLabel.setHorizontalAlignment(JLabel.CENTER);
		this.rowCountLabel = new JLabel();
		this.model = new DefaultTableModel() {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int column) {
				return column == 1;
			}
		};

		this.model.setColumnIdentifiers(new Object[] { "Nome", "Telefone" });
		this.model.addTableModelListener(new TableModelListener() {
			@Override
			public void tableChanged(TableModelEvent e) {
				editItem_Event(e);
			}
		});

		this.contactsTable = new JTable();
		this.contactsTable.setModel(model);
		this.sorter = new TableRowSorter<TableModel>(contactsTable.getModel());
		this.contactsTable.setRowSorter(sorter);

		JPopupMenu tablePopupMenu = new JPopupMenu();
		JMenuItem refreshItem = new JMenuItem("Atualizar");
		refreshItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				refreshItem_Click();
			}
		});
		JMenuItem deleteItem = new JMenuItem("Deletar");
		deleteItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				deleteItem_Click();
			}
		});
		tablePopupMenu.add(refreshItem);
		tablePopupMenu.add(deleteItem);

		this.contactsTable.setComponentPopupMenu(tablePopupMenu);
		this.contactsScrollPane = new JScrollPane(contactsTable);
		this.searchTextField = new TextField("Pesquise os contatos pelo nome aqui...", 45);
		this.searchTextField.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void removeUpdate(DocumentEvent e) {
				searchTextField_Changed();
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				searchTextField_Changed();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {

			}
		});

		this.inputPanel = new JPanel();
		this.contactsPanel = new JPanel(new BorderLayout());
		this.statusPanel = new JPanel(new BorderLayout());
	}

	/**
	 * Method to add components to frame.
	 * 
	 * @param contentPane
	 *            The container that will receive all components.
	 */
	protected void addComponents(Container contentPane) {
		this.inputPanel.add(nameTextField);
		this.inputPanel.add(numberTextField);
		this.inputPanel.add(addButton);
		this.inputPanel.setBorder(BorderFactory.createTitledBorder("Adicionar contato"));
		this.contactsTable.setFillsViewportHeight(true);
		JPanel searchPanel = new JPanel();
		searchPanel.setBorder(BorderFactory.createTitledBorder("Buscar contatos"));
		searchPanel.add(this.searchTextField);
		this.contactsPanel.add(searchPanel, BorderLayout.NORTH);
		this.contactsPanel.add(contactsScrollPane, BorderLayout.CENTER);
		this.statusPanel.add(agendaLabel, BorderLayout.WEST);
		this.statusPanel.add(statusLabel, BorderLayout.CENTER);
		this.statusPanel.add(rowCountLabel, BorderLayout.EAST);
		LayoutManager layout = new BorderLayout();
		JPanel panel = new JPanel(layout);
		panel.add(this.inputPanel, BorderLayout.NORTH);
		panel.add(this.contactsPanel, BorderLayout.CENTER);
		panel.add(this.statusPanel, BorderLayout.SOUTH);
		contentPane.add(panel);
		contentPane.setPreferredSize(new Dimension(600, 500));
	}

	/**
	 * Method used to start the window.
	 * 
	 * @throws Exception
	 *             Any error happened while this window is running.
	 */
	public void start() throws Exception {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				addComponents(frame.getContentPane());

				frame.setTitle("Agenda");
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setLocationByPlatform(true);
				frame.pack();
				frame.setResizable(false);
				frame.setVisible(true);
			}
		});
		
		
		StringBuilder messageTxt = new StringBuilder();
		messageTxt.append("Funções da agenda:\n");
		messageTxt.append("\t1 - É possivel inserir contatos na agenda a partir do botão \"Adiconar\". \n");
		messageTxt.append("\t2 - Após inserido, um contato pode ter seu número atualizado, bastando\n");
		messageTxt.append("pressionar a tecla \"F2\" em cima da célula correspondente ao númenro\n");
		messageTxt.append("e ao terminar pressionar \"Enter\".\n");
		messageTxt.append("\t3 - Os contatos podem ser deletados da tabela, para isso basta seleciona-los\n");
		messageTxt.append("e clicar em \"Deletar\" no menu de contexto da tabela acessado através do botão\n");
		messageTxt.append("direito do mouse.\n");
		messageTxt.append("\t4 - No menu de contexto da tabela também existe uma opção \"Atualizar\", que\n");
		messageTxt.append("significa receber as alterações das demais instâncias da agenda.\n");
		messageTxt.append("\t5 - A consulta dos contatos pode ser feita pelo nome deles no painel de busca.\n");
		messageTxt.append("\t6 - Um clique no cabeçalho da tabela de contatos pode ordernar a lista.\n");
		messageTxt.append("\t7 - A barra de status da agenda exibe as seguintes informações: instância\n");
		messageTxt.append("que esta sendo utilizada, data da úlima sincronização e o total de contatos\n");
		messageTxt.append("que a agenda possui.\n");
		messageTxt.append("\nSelecione uma instância para utilizar a agenda:\n");

		this.showStartupDialog(messageTxt.toString(), "Bem vindo!", frame);
		this.loadContacts();
	}

	/**
	 * Shows a dialog used to pick up a instance of agenda.
	 * 
	 * @param frame
	 *            Dialog parent.
	 * @throws Exception
	 *             When user didn't selected a instance or couldn't load the
	 *             instance selected, if each one happens this window will
	 *             broke.
	 */
	protected void showStartupDialog(String message, String title, JFrame frame) throws Exception {
		Object[] possibilities = this.loadAllAgendaNames();

		String s = (String) JOptionPane.showInputDialog(frame, message, title, JOptionPane.PLAIN_MESSAGE, null, possibilities, possibilities[0]);

		if ((s != null) && (s.length() > 0)) {
			this.agendaLabel.setText(s);
			this.agendaName = s;
			this.agenda = this.loadAgenda(s);
		} else {
			throw new Exception("Error! User didn't selected a instance!");
		}
	}

	/**
	 * Loads the selected instance of agenda.
	 * 
	 * @param s
	 *            The name used to retrieve object reference.
	 * @throws Exception
	 *             When couldn't load the object.
	 */
	protected Agenda loadAgenda(String s) throws Exception {
		NameComponent[] name = { new NameComponent(s, "Agenda") };
		org.omg.CORBA.Object objRef = namingContext.resolve(name);
		Agenda agenda = AgendaHelper.narrow(objRef);
		return agenda;
	}

	protected Object[] loadAllAgendaNames() {
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

	private void loadContacts() {

		try {
			List<Contact> contacts = Arrays.asList(this.agenda.getContacts());

			for (Contact c : contacts) {
				this.model.addRow(new Object[] { c.name, c.phoneNumber });
			}

			this.statusLabel.setText("Última sincronização: " + new SimpleDateFormat("dd/MM/yy HH:mm:ss").format(new Date()));

		} catch (Exception e) {
			handleConnectException(e);
		}

	}

	private void handleConnectException(Exception e) {
		e.printStackTrace();

		if (e.getCause() instanceof java.net.ConnectException) {

			this.statusLabel.setText("Ocorreu um erro ao executar operação.");
			this.agendaLabel.setText("Nenhuma agenda.");

			NameComponent[] nameComponents = { new NameComponent(this.agendaName, "Agenda") };

			try {
				namingContext.unbind(nameComponents);
			} catch (Exception e1) {
			}

			try {
				
				StringBuilder message = new StringBuilder();
				 message.append("Um erro aconteceu ao tentar sincronizar dados com a instância que você estava utilizando.\n");
				 message.append("\nSelecione a instância para sincronizar os dados a partir de agora:");
				
				this.showStartupDialog(message.toString() , "Recuperação da agenda", frame);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
	}

	private void addButton_Click() {
		String name = this.nameTextField.getText();
		String number = this.numberTextField.getText();

		if (!"".equals(name) && !"".equals(number)) {
			Contact c = new Contact(name, number);

			try {
				this.agenda.addContact(c);
				this.agenda.syncContact(c, new String[] { agenda.getName() }, constants.Action.INSERT);
				this.model.addRow(new Object[] { name, number });
				this.nameTextField.setText("");
				this.numberTextField.setText("");
			} catch (ContactAlreadyExists e1) {
				JOptionPane.showMessageDialog(frame, "O contato já existe na agenda.", "Erro", JOptionPane.ERROR_MESSAGE);
			} catch (Exception e) {
				handleConnectException(e);
			}

		} else {
			JOptionPane.showMessageDialog(frame, "Informe o nome e número do contato para inseri-lo.", "Atenção", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void searchTextField_Changed() {
		try {
			this.sorter.setRowFilter(RowFilter.regexFilter(this.searchTextField.getText(), 0));
		} catch (java.util.regex.PatternSyntaxException e) {
			return;
		}
	}

	private void deleteItem_Click() {

		int[] selectedRows = this.contactsTable.getSelectedRows();

		if (selectedRows.length > 0) {

			for (int i = selectedRows.length - 1; i >= 0; i--) {
				String name = (String) this.contactsTable.getValueAt(i, 0);
				String number = (String) this.contactsTable.getValueAt(i, 1);
				Contact currentContact = new Contact(name, number);

				try {
					this.agenda.deleteContact(currentContact);
					this.agenda.syncContact(currentContact, new String[] { agenda.getName() }, constants.Action.DELETE);
					this.model.removeRow(this.getRowByValue(model, name));
				} catch (Exception e) {
					handleConnectException(e);
				}
			}
		}
	}

	protected void refreshItem_Click() {
		this.model.setRowCount(0);
		this.loadContacts();
	}

	private void editItem_Event(TableModelEvent e) {

		switch (e.getType()) {

		case TableModelEvent.INSERT:
			this.rowCountLabel.setText("Total: " + this.contactsTable.getRowCount());
			this.statusLabel.setText("Última sincronização: " + new SimpleDateFormat("dd/MM/yy HH:mm:ss").format(new Date()));
			break;
		case TableModelEvent.UPDATE:
			int row = e.getFirstRow();
			String name = (String) this.contactsTable.getValueAt(row, 0);
			String number = (String) this.contactsTable.getValueAt(row, 1);
			Contact currentContact = new Contact(name, number);

			try {
				this.agenda.updateContact(currentContact);
				this.agenda.syncContact(currentContact, new String[] { agenda.getName() }, constants.Action.UPDATE);
				this.statusLabel.setText("Última sincronização: " + new SimpleDateFormat("dd/MM/yy HH:mm:ss").format(new Date()));
			} catch (Exception ex) {
				handleConnectException(ex);
			}

			break;
		case TableModelEvent.DELETE:
			this.rowCountLabel.setText("Total: " + this.contactsTable.getRowCount());
			this.statusLabel.setText("Última sincronização: " + new SimpleDateFormat("dd/MM/yy HH:mm:ss").format(new Date()));
			break;
		}
	}

	private int getRowByValue(TableModel model, Object value) {
		for (int i = model.getRowCount() - 1; i >= 0; --i) {
			for (int j = model.getColumnCount() - 1; j >= 0; --j) {
				if (model.getValueAt(i, j).equals(value)) {
					return i;
				}
			}
		}
		return -1;
	}

}
