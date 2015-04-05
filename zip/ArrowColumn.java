import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractCellEditor;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.plaf.basic.BasicArrowButton;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;


public class ArrowColumn extends AbstractCellEditor implements
		TableCellRenderer, TableCellEditor, ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	JTable table;
	BasicArrowButton editButton;
	BasicArrowButton renderButton;

	
	public ArrowColumn(JTable table, int column, int direction){
		super();
		this.table = table;
		
		renderButton = new BasicArrowButton(direction);
		editButton = new BasicArrowButton(direction);
		editButton.setFocusPainted(true);
		editButton.addActionListener(this);
		editButton.setText("arrow");
		TableColumnModel columnModel = table.getColumnModel();
		columnModel.getColumn(column).setCellRenderer(this);
		columnModel.getColumn(column).setCellEditor(this);
	}
	

	@Override
	public Object getCellEditorValue() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public Component getTableCellEditorComponent(JTable table, Object value,
			boolean isSelected, int hasFocus, int row) {
		// TODO Auto-generated method stub
		return editButton;
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		// TODO Auto-generated method stub

		 if (hasFocus)
         {
             renderButton.setForeground(table.getForeground());
             renderButton.setBackground(UIManager.getColor("Button.background"));
         }
         else if (isSelected)
         {
             renderButton.setForeground(table.getSelectionForeground());
              renderButton.setBackground(table.getSelectionBackground());
         }
         else
         {
             renderButton.setForeground(table.getForeground());
             renderButton.setBackground(UIManager.getColor("Button.background"));
         }
		 //renderButton.setText("up");
		return renderButton;
	}

}
