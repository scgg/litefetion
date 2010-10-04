 /*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

 /**
 * Project  : LiteFetion
 * Package  : net.solosky.litefetion
 * File     : VerifyDialog.java
 * Author   : solosky < solosky772@qq.com >
 * Created  : 2010-10-4
 * License  : Apache License 2.0 
 */
package net.solosky.litefetion;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.concurrent.CountDownLatch;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JTextField;

import net.solosky.litefetion.bean.VerifyImage;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 *
 *
 * @author solosky <solosky772@qq.com>
 */
public class VerifyDialog extends JDialog
{

	private final JPanel contentPanel = new JPanel();
	private JTextField verifyField;
	private JLabel verifyLabel;
	private VerifyImage verifyImage;
	private LiteFetion  liteFetion;
	private CountDownLatch latch;
	
	

	/**
     * @param verifyImage
     * @param liteFetion
     */
    public VerifyDialog(VerifyImage verifyImage, LiteFetion liteFetion) {
	    this();
	    this.verifyImage = verifyImage;
	    this.liteFetion = liteFetion;
	    this.latch = new CountDownLatch(1);
	    this.verifyLabel.setIcon(new ImageIcon(verifyImage.getImageData()));
	    this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    }

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			VerifyDialog dialog = new VerifyDialog();
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public VerifyImage waitOK() {
		try {
	        this.latch.await();
        } catch (InterruptedException e) {
	        e.printStackTrace();
        }
		return this.verifyImage;
	}

	/**
	 * Create the dialog.
	 */
	public VerifyDialog() {
		setTitle("验证码输入框");
		setBounds(100, 100, 198, 167);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		{
			JLabel label = new JLabel("请输入验证码：");
			label.setBounds(12, 10, 127, 15);
			contentPanel.add(label);
		}
		
		verifyLabel = new JLabel("");
		verifyLabel.setBounds(22, 35, 117, 32);
		contentPanel.add(verifyLabel);
		
		verifyField = new JTextField();
		verifyField.setBounds(23, 74, 116, 21);
		contentPanel.add(verifyField);
		verifyField.setColumns(10);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton flushButton = new JButton("刷新");
				flushButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						verifyImage = liteFetion.retireVerifyImage(verifyImage.getVerifyType());
						verifyLabel.setIcon(new ImageIcon(verifyImage.getImageData()));
					}
				});
				flushButton.setActionCommand("Cancel");
				buttonPane.add(flushButton);
			}
			{
				JButton okButton = new JButton("确定");
				final VerifyDialog dialog = this;
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						verifyImage.setVerifyCode(verifyField.getText());
						latch.countDown();
						dialog.dispose();
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
		}
	}
}
