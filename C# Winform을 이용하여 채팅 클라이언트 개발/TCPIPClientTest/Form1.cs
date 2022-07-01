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
        private Socket socket;  // 소켓
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
            // 서버 연결
            IPAddress ipaddress = IPAddress.Parse(textBox1.Text);
            IPEndPoint endPoint = new IPEndPoint(ipaddress, int.Parse(textBox2.Text));

            socket = new Socket(
                AddressFamily.InterNetwork,
                SocketType.Stream,
                ProtocolType.Tcp
                );


            // 연결하기
            Log("서버에 연결 시도중...");
            socket.Connect(endPoint);
            Log("서버에 접속됨");


            //닉네임 받기
            if (textBox4.Text.Trim() != "")
            {
                NetworkStream ns = new NetworkStream(socket);

                byte[] name = Encoding.UTF8.GetBytes(textBox4.Text);
                socket.Send(name);
                Log("닉네임: " + textBox4.Text);
            }
        }
        
        // 송수신 메시지들 대화창에 출력
        private void ShowMsg(string msg)
        {
            richTextBox1.AppendText(msg + "\r\n");
            //richTextBox1.AppendText("\r\n");

            // 스크롤을 내리기.
            this.Activate();

            // 캐럿(커서)를 텍스트박스의 끝으로 내림
            richTextBox1.SelectionStart = richTextBox1.Text.Length;
            richTextBox1.ScrollToCaret();   // 스크럴을 캐럿(커서)위치에 맞춤
        }

        private void button2_Click(object sender, EventArgs e)
        {
            // 메시지 전송하기(공백이 아닐때)
            if (textBox3.Text.Trim() != "")
            {
                byte[] sendscanner = Encoding.UTF8.GetBytes(textBox3.Text);
                socket.Send(sendscanner);
                Log("메시지 전송됨");

                byte[] name = Encoding.UTF8.GetBytes(textBox3.Text);
                ShowMsg("나]" + textBox3.Text);
                // 초기화
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

        // 지금은 두번의 함수를 타고 엔터가 실행되는데 비효율적임.
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

        //combobx처리
        private void comboBox1_SelectedIndexChanged(object sender, EventArgs e)
        {
            //richTextBox1의 maximum을 맞춰줌..
            int maxNum = 1000;
            string max = comboBox1.Text;
            if (max == data[0])
            {
                //현재 커서의 위치=길이
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