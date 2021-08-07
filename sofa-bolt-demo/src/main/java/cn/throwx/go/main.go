package main

import (
	"bytes"
	"encoding/binary"
	"encoding/json"
	"fmt"
	"net"
)

func main() {
	con, err := net.Dial("tcp", "127.0.0.1:9999")
	if err != nil {
		fmt.Println("err:", err)
		return
	}
	defer con.Close()
	req := &Req{
		Id:   8080,
		Name: "throwx.cn",
	}
	content, err := json.Marshal(req)
	if err != nil {
		fmt.Println("err:", err)
		return
	}
	var header []byte
	className := []byte("com.alipay.remoting.Req")
	cmd := &RequestCommand{
		ProtocolCode:    2,
		ProtocolVersion: 2,
		Type:            1,
		CommandCode:     1,
		CommandVersion:  1,
		RequestId:       10087,
		Codec:           1,
		Switch:          0,
		Timeout:         5000,
		ClassLength:     uint16(len(className)),
		HeaderLength:    0,
		ContentLength:   uint32(len(content)),
		ClassName:       className,
		Header:          header,
		Content:         content,
	}
	pkg := encode(cmd)
	_, err = con.Write(pkg)
	if err != nil {
		fmt.Println("err:", err)
		return
	}
	fmt.Println("发送请求成功")
}

type Req struct {
	Id   int64  `json:"id"`
	Name string `json:"name"`
}

// encode req => slice
func encode(cmd *RequestCommand) []byte {
	container := make([]byte, 0)
	buf := bytes.NewBuffer(container)
	buf.WriteByte(cmd.ProtocolCode)
	buf.WriteByte(cmd.ProtocolVersion)
	buf.WriteByte(cmd.Type)
	binary.Write(buf, binary.BigEndian, cmd.CommandCode)
	buf.WriteByte(cmd.CommandVersion)
	binary.Write(buf, binary.BigEndian, cmd.RequestId)
	buf.WriteByte(cmd.Codec)
	buf.WriteByte(cmd.Switch)
	binary.Write(buf, binary.BigEndian, cmd.Timeout)
	binary.Write(buf, binary.BigEndian, cmd.ClassLength)
	binary.Write(buf, binary.BigEndian, cmd.HeaderLength)
	binary.Write(buf, binary.BigEndian, cmd.ContentLength)
	buf.Write(cmd.ClassName)
	buf.Write(cmd.Header)
	buf.Write(cmd.Content)
	return buf.Bytes()
}

// RequestCommand sofa-bolt v2 req cmd
type RequestCommand struct {
	ProtocolCode    uint8
	ProtocolVersion uint8
	Type            uint8
	CommandCode     uint16
	CommandVersion  uint8
	RequestId       uint32
	Codec           uint8
	Switch          uint8
	Timeout         uint32
	ClassLength     uint16
	HeaderLength    uint16
	ContentLength   uint32
	ClassName       []byte
	Header          []byte
	Content         []byte
}
