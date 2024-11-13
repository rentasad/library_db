# Dockerfile für ssh_server
FROM ubuntu:latest

# Installiere OpenSSH-Server, rsyslog und weitere nützliche Tools
RUN apt-get update && \
    apt-get install -y openssh-server rsyslog vim mc dos2unix mysql-client

# Konfiguriere rsyslog, um die Protokollierung zu ermöglichen
RUN sed -i '/imklog/s/^/#/' /etc/rsyslog.conf && \
    echo 'auth,authpriv.* /var/log/auth.log' >> /etc/rsyslog.conf && \
    echo '*.* /var/log/syslog' >> /etc/rsyslog.conf

# Konfiguriere SSHD
RUN mkdir /var/run/sshd && \
    mkdir -p /root/.ssh && \
    chmod 700 /root/.ssh

# Kopiere die Schlüssel und Konfigurationsdateien ins Image
COPY authorized_keys /root/.ssh/authorized_keys
COPY sshd_config /etc/ssh/sshd_config

# Setze die korrekten Berechtigungen für authorized_keys
RUN chmod 600 /root/.ssh/authorized_keys && \
    chown root:root /root/.ssh/authorized_keys && \
	dos2unix /root/.ssh/authorized_keys

# Starten von rsyslog und SSHD
CMD rsyslogd && /usr/sbin/sshd -D
