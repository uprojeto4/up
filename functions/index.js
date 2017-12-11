const functions = require('firebase-functions');
const admin = require('firebase-admin');

admin.initializeApp(functions.config().firebase);

exports.sendNewMessageNotification = functions.database.ref('/negotiations/{user_id}/{negotiation_id}/unreadMessagesCounter').onWrite(event => {
	
	// obtem o id do usuario e do anuncio onde ocorreram mudancas na negociacao
    const user_id = event.params.user_id;
	const negotiation_id = event.params.negotiation_id;
    
    if (event.data.val()) {
		
		// obtem demais dados da negociacao
		const negotiationMetadata = admin.database().ref(`/negotiations/${user_id}/${negotiation_id}`).once('value');
	
		return negotiationMetadata.then(negotiationResult => {
			
			// obtem demais dados da negociacao
			const ad_id = negotiationResult.val().adId;
			const lastMessage = negotiationResult.val().lastMessage;
			const lastMessageSenderId = negotiationResult.val().lastMessageSenderId;
			const unreadMessages = negotiationResult.val().unreadMessagesCounter;
			const remoteUserId = negotiationResult.val().remoteUserId;
			const vendorId = negotiationResult.val().vendorId;
			
			let negotiationType = 0;
	
			if (user_id != vendorId) {
				negotiationType = 1;
			}
			
			if (user_id == lastMessageSenderId || unreadMessages == 0) {
				
				return console.log("Bypassing notification to user " + user_id);
				
			} else {
				
				// obtem o titulo da negociacao
				const adTitleQuery = admin.database().ref(`/posts/${ad_id}/title`).once('value');
		
				return adTitleQuery.then(adTitleQueryResult => {
		
					const adTitle = adTitleQueryResult.val();
		
					// obtem o nome do usuario que enviou a mensagem
					const messageSenderQuery = admin.database().ref(`/users/${lastMessageSenderId}/nome`).once('value');
					
					return messageSenderQuery.then(userResult => {
						
						const userName  = userResult.val();
		
						// obtem o token do dispositivo associado ao usuario
						const deviceToken = admin.database().ref(`/users/${user_id}/device_token`).once('value');
			
						return deviceToken.then(result => {
			
							const token_id  = result.val();
							const payload = {
								notification: {
									title: `Nova mensagem em ${adTitle}`,
									body: `${userName}: ${lastMessage}`,
									icon: "default",
									click_action: "br.ufc.quixada.up_TARGET_NOTIFICATION",
									tag: ad_id,
									sound: "default"
								},
								data: {
									adId: ad_id,
									remoteUserId: remoteUserId,
									negotiationType: negotiationType.toString(),
									unreadMessages: unreadMessages.toString(),
									adTitle: adTitle;
								}
							};
						
							return admin.messaging().sendToDevice(token_id, payload).then(response => {
								console.log("Notification sent to ", user_id);
							});
			
						});
					
					});
		
				});
			
			};

		});

	}
	
});
