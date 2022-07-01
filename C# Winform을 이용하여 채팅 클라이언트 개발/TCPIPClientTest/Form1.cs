using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Net;
using System.Net.Sockets;
using System.Text;
using System.Windows.Forms;

namespace TCPIPClientTest
{
    public partial class Form1 : Form
    {
        private Socket socket;  // ����
        string[] data = { "ALL", "50", "100", "200", "500" };
        public Form1()
        {
            InitializeComponent();
            comboBox1.Items.AddRange(data);
            comboBox1.SelectedIndex = 0;
        }

        private void Form1_Load(object sender, EventArgs e) {}

        private void Log(string msg)
        {
            listBox1.Items.Add(string.Format("[{0}]{1}", DateTime.Now.ToString(), msg));
        }


        private void button1_Click_1(object sender, EventArgs e)
        {
            // ���� ����
            IPAddress ipaddress = IPAddress.Parse(textBox1.Text);
            IPEndPoint endPoint = new IPEndPoint(ipaddress, int.Parse(textBox2.Text));

            socket = new Socket(
                AddressFamily.InterNetwork,
                SocketType.Stream,
                ProtocolType.Tcp
                );


            // �����ϱ�
            Log("������ ���� �õ���...");
            socket.Connect(endPoint);
            Log("������ ���ӵ�");


            //�г��� �ޱ�
            if (textBox4.Text.Trim() != "")
            {
                NetworkStream ns = new NetworkStream(socket);

                byte[] name = Encoding.UTF8.GetBytes(textBox4.Text);
                socket.Send(name);
                Log("�г���: " + textBox4.Text);
            }
        }
        
        // �ۼ��� �޽����� ��ȭâ�� ���
        private void ShowMsg(string msg)
        {
            richTextBox1.AppendText(msg + "\r\n");
            //richTextBox1.AppendText("\r\n");

            // ��ũ���� ������.
            this.Activate();

            // ĳ��(Ŀ��)�� �ؽ�Ʈ�ڽ��� ������ ����
            richTextBox1.SelectionStart = richTextBox1.Text.Length;
            richTextBox1.ScrollToCaret();   // ��ũ���� ĳ��(Ŀ��)��ġ�� ����
        }

        private void button2_Click(object sender, EventArgs e)
        {
            // �޽��� �����ϱ�(������ �ƴҶ�)
            if (textBox3.Text.Trim() != "")
            {
                byte[] sendscanner = Encoding.UTF8.GetBytes(textBox3.Text);
                socket.Send(sendscanner);
                Log("�޽��� ���۵�");

                byte[] name = Encoding.UTF8.GetBytes(textBox3.Text);
                ShowMsg("��]" + textBox3.Text);
                // �ʱ�ȭ
                textBox3.Text = "";
            }
        }

        private void btn_Enter(object sender, KeyEventArgs e)
        {
            bool temp = textBox2.Focused;
            bool temp2 = textBox3.Focused;


            if (textBox2.Focused)
            {
                if (e.KeyCode == Keys.Enter)
                {
                    this.button1_Click_1(sender, e);
                }
            }
            else if (textBox3.Focused)
            {
                if (e.KeyCode == Keys.Enter)
                {
                    this.button2_Click(sender, e);
                }
            }

        }

        // ������ �ι��� �Լ��� Ÿ�� ���Ͱ� ����Ǵµ� ��ȿ������.
        private void textBox4_KeyDown(object sender, KeyEventArgs e)
        {

            if (e.KeyCode == Keys.Enter)
            {
                //this.btn_Enter(sender, e);
                //this.button1_Click_1(sender, e);
                this.button1.PerformClick();
            }
        }

        private void textBox3_KeyDown(object sender, KeyEventArgs e)
        {
            if (e.KeyCode == Keys.Enter)
            {
                this.btn_Enter(sender, e);
            }
        }

        //combobxó��
        private void comboBox1_SelectedIndexChanged(object sender, EventArgs e)
        {
            //richTextBox1�� maximum�� ������..
            int maxNum = 1000;
            string max = comboBox1.Text;
            if (max == data[0])
            {
                //���� Ŀ���� ��ġ=����
                richTextBox1.SelectionStart = richTextBox1.Text.Length;
                richTextBox1.ScrollToCaret();
            }
            else
            {
                maxNum = int.Parse(comboBox1.Text);
                if (richTextBox1.Lines.Length > maxNum)
                {
                    string[] newLines = new string[maxNum];
                    Array.Copy(richTextBox1.Lines, richTextBox1.Lines.Length - maxNum - 1, newLines, 0, maxNum);
                    richTextBox1.Lines = newLines;
                }
                richTextBox1.SelectionStart = richTextBox1.Text.Length;
                richTextBox1.ScrollToCaret();
            }
        }

    }
}