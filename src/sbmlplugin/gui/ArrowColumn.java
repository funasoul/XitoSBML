/*******************************************************************************
 * Copyright 2015 Kaito Ii
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package sbmlplugin.gui;

import javax.swing.JTable;
import javax.swing.plaf.basic.BasicArrowButton;


@SuppressWarnings({ "serial" })
public class ArrowColumn extends ButtonColumn{

	JTable table;
	BasicArrowButton editButton;
	BasicArrowButton renderButton;

	public ArrowColumn(JTable table, int column, int direction){
		super(table, column);
		renderButton = new BasicArrowButton(direction);
		editButton = new BasicArrowButton(direction);
		editButton.setText("arrow");
		setButtons(renderButton, editButton);
	}
}
