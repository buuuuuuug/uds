#!/usr/bin/env python3
# -*- coding: utf-8 -*-

import argparse
import socket
import sys
import os
import threading
import time
from concurrent.futures import ThreadPoolExecutor, as_completed

def send_message_to_socket(socket_path, message, thread_id=0):
    """
    向Unix域套接字发送单条消息

    Args:
        socket_path (str): socket文件路径
        message (str): 要发送的消息
        thread_id (int): 线程ID（用于标识）

    Returns:
        tuple: (thread_id, success, response)
    """
    # 检查socket文件是否存在
    if not os.path.exists(socket_path):
        return (thread_id, False, f"错误: socket文件 '{socket_path}' 不存在")

    # 创建Unix域套接字
    sock = socket.socket(socket.AF_UNIX, socket.SOCK_STREAM)

    try:
        # 连接到socket
        wait_time = 10
        sock.connect(socket_path)
        print(f"已连接到socket: {socket_path}, 等待{wait_time}s")
        time.sleep(wait_time)
        # 发送消息
        print(f"准备发送消息: {message}")
        sock.sendall(message.encode('utf-8'))

        time.sleep(0.2)

    except socket.error as e:
        return thread_id, False, f"Socket错误: {e}"
    finally:
        # 关闭套接字
        sock.close()
        print(f"已关闭socket: {socket_path}")

def create_unix_socket_client_multithread(socket_path, message, count=1, threads=1):
    """
    使用多线程向Unix域套接字发送消息

    Args:
        socket_path (str): socket文件路径
        message (str): 要发送的消息
        count (int): 总发送次数
        threads (int): 并发线程数
    """
    print(f"正在启动 {threads} 个线程，总共发送 {count} 条消息到 {socket_path}")

    # 验证socket文件是否存在
    if not os.path.exists(socket_path):
        print(f"错误: socket文件 '{socket_path}' 不存在")
        return False

    start_time = time.time()
    success_count = 0

    # 使用线程池执行器
    with ThreadPoolExecutor(max_workers=threads) as executor:
        # 提交任务
        futures = []
        for i in range(count):
            future = executor.submit(send_message_to_socket, socket_path, message, i)
            futures.append(future)

    end_time = time.time()
    elapsed_time = end_time - start_time

    print(f"\n执行完成:")

    return success_count == count

def main():
    # 创建参数解析器
    parser = argparse.ArgumentParser(
        description="Unix域套接字客户端工具（支持多线程并发）",
        formatter_class=argparse.RawDescriptionHelpFormatter,
        epilog="""
使用示例:
  %(prog)s -s /tmp/mysocket.sock -m "Hello World"
  %(prog)s -s /tmp/mysocket.sock -m "Test Message" -c 10 -t 5
        """
    )

    # 添加参数
    parser.add_argument(
        '-s', '--socket',
        required=True,
        help='Unix域套接字文件路径 (例如: /tmp/mysocket.sock)'
    )

    parser.add_argument(
        '-m', '--message',
        required=True,
        help='要发送的消息内容'
    )

    parser.add_argument(
        '-c', '--count',
        type=int,
        default=1,
        help='发送消息的总次数 (默认: 1)'
    )

    parser.add_argument(
        '-t', '--threads',
        type=int,
        default=1,
        help='并发线程数 (默认: 1)'
    )

    # 解析命令行参数
    args = parser.parse_args()

    # 验证参数
    if args.count <= 0:
        print("错误: 发送次数必须大于0")
        sys.exit(1)

    if args.threads <= 0:
        print("错误: 线程数必须大于0")
        sys.exit(1)

    if args.threads > args.count:
        print("警告: 线程数大于发送次数，将调整为发送次数")
        args.threads = args.count

    if not args.message:
        print("错误: 消息内容不能为空")
        sys.exit(1)

    # 执行主要功能
    success = create_unix_socket_client_multithread(
        socket_path=args.socket,
        message=args.message,
        count=args.count,
        threads=args.threads
    )

    # 根据执行结果退出
    if success:
        print("所有消息发送成功")
        sys.exit(0)
    else:
        print("部分或全部消息发送失败")
        sys.exit(1)

if __name__ == "__main__":
    main()
